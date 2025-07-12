package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.record.dto.RecordResponse;
import org.cotato.csquiz.api.record.dto.RecordsAndScorerResponse;
import org.cotato.csquiz.api.record.dto.RegradeRequest;
import org.cotato.csquiz.api.record.dto.ReplyResponse;
import org.cotato.csquiz.api.record.dto.ScorerResponse;
import org.cotato.csquiz.api.socket.dto.QuizSocketRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.component.GenerationMemberAuthValidator;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.education.cache.QuizAnswerRedisRepository;
import org.cotato.csquiz.domain.education.cache.TicketCountRedisRepository;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Record;
import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.education.facade.RedissonScorerFacade;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.repository.RecordRepository;
import org.cotato.csquiz.domain.education.repository.ScorerRepository;
import org.cotato.csquiz.domain.education.service.component.QuizReader;
import org.cotato.csquiz.domain.education.util.AnswerUtil;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private static final String INPUT_DELIMITER = ",";
    private final RedissonScorerFacade redissonScorerFacade;
    private final MemberService memberService;
    private final RecordRepository recordRepository;
    private final QuizRepository quizRepository;
    private final ScorerRepository scorerRepository;
    private final QuizAnswerRedisRepository quizAnswerRedisRepository;
    private final TicketCountRedisRepository ticketCountRedisRepository;
    private final GenerationMemberAuthValidator generationMemberAuthValidator;
    private final GenerationReader generationReader;
    private final QuizReader quizReader;


    @Transactional
    public ReplyResponse replyToQuiz(final Long quizId, final List<String> inputs, final Member member) {
        final Long ticketNumber = ticketCountRedisRepository.increment(quizId);
        Quiz quiz = quizReader.getById(quizId);

        Generation generation = generationReader.findById(quiz.getEducation().getGenerationId());

        generationMemberAuthValidator.checkGenerationPermission(member, generation);
        checkQuizStart(quiz);

        checkMemberAlreadyCorrect(quiz, member);
        List<String> processedInputs = inputs.stream()
                .map(AnswerUtil::processAnswer)
                .sorted()
                .toList();

        boolean isCorrect = quizAnswerRedisRepository.isCorrect(quiz, inputs);

        String reply = String.join(INPUT_DELIMITER, processedInputs);
        Record createdRecord = Record.of(reply, isCorrect, member, quiz, ticketNumber);

       if (isCorrect) {
           redissonScorerFacade.checkAndThenUpdateScorer(createdRecord);
       }

        recordRepository.save(createdRecord);
        return ReplyResponse.from(isCorrect);
    }

    private void checkQuizStart(Quiz findQuiz) {
        if (findQuiz.isOff() || !findQuiz.isStart()) {
            throw new AppException(ErrorCode.QUIZ_ACCESS_DENIED);
        }
    }

    private void checkMemberAlreadyCorrect(Quiz findQuiz, Member findMember) {
        if (recordRepository.existsByQuizAndMemberIdAndIsCorrect(findQuiz, findMember.getId(), true)) {
            log.warn("이미 해당 문제에 정답 제출한 사용자입니다.");
            log.warn("문제 번호: {}, 제출한 멤버: {}", findQuiz.getNumber(), findMember.getName());
            throw new AppException(ErrorCode.ALREADY_REPLY_CORRECT);
        }
    }

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("해당 문제를 찾을 수 없습니다."));
    }

    public void saveAnswersToCache(final Long educationId) {
        List<Quiz> quizzes = quizRepository.findAllByEducationId(educationId);

        quizAnswerRedisRepository.saveAllQuizAnswers(quizzes);
    }

    @Transactional
    public void regradeRecords(RegradeRequest request) {
        Quiz quiz = findQuizById(request.quizId());
        checkQuizType(quiz);
        List<Record> correctRecords = recordRepository.findAllByQuizAndReply(quiz, request.newAnswer());

        correctRecords.forEach(record -> record.updateCorrect(true));
        recordRepository.saveAll(correctRecords);

        Record fastestRecord = correctRecords.stream()
                .min(Comparator.comparing(Record::getTicketNumber))
                .orElseThrow(() -> new AppException(ErrorCode.REGRADE_FAIL));

        // 기존 득점자가 있어 -> 비교 후 업데이트
        // 없어 -> 본인을 득점자로 등록
        redissonScorerFacade.checkAndThenUpdateScorer(fastestRecord);
    }

    private void checkQuizType(Quiz quiz) {
        if (quiz instanceof MultipleQuiz) {
            throw new AppException(ErrorCode.QUIZ_TYPE_NOT_MATCH);
        }
    }

    @Transactional
    public RecordsAndScorerResponse findRecordsAndScorer(Long quizId) {
        Quiz findQuiz = findQuizById(quizId);
        List<RecordResponse> records = findAllRecordByQuiz(findQuiz);

        Optional<Scorer> maybeScorer = scorerRepository.findByQuizId(findQuiz.getId());
        if (maybeScorer.isPresent()) {
            Member findMember = memberService.findById(maybeScorer.get().getMemberId());
            ScorerResponse scorerResponse = ScorerResponse.of(maybeScorer.get(), findMember,
                    memberService.findBackFourNumber(findMember));
            log.info("[기존 득점자 존재]: {}", findMember.getName());
            return RecordsAndScorerResponse.from(records, scorerResponse);
        }
        log.info("[응답과 득점자 반환 서비스]");
        return RecordsAndScorerResponse.from(records, null);
    }

    private List<RecordResponse> findAllRecordByQuiz(Quiz quiz) {
        List<Record> records = recordRepository.findAllFetchJoin();
        log.info("[문제에 모든 응답 반환 서비스]");
        return records.stream()
                .filter(record -> record.getQuiz().equals(quiz))
                .sorted(Comparator.comparing(Record::getTicketNumber))
                .map(record -> RecordResponse.of(record, memberService.getMemberInfo(record.getMemberId())))
                .toList();
    }

    @Transactional
    public void saveAnswer(QuizSocketRequest request) {
        Quiz findQuiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈를 찾을 수 없습니다."));
        quizAnswerRedisRepository.saveQuizAnswer(findQuiz);
    }
}
