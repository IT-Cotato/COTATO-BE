package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.quiz.dto.AddAdditionalAnswerRequest;
import org.cotato.csquiz.api.record.dto.RecordResponse;
import org.cotato.csquiz.api.record.dto.RecordsAndScorerResponse;
import org.cotato.csquiz.api.record.dto.RegradeRequest;
import org.cotato.csquiz.api.record.dto.ReplyRequest;
import org.cotato.csquiz.api.record.dto.ReplyResponse;
import org.cotato.csquiz.api.record.dto.ScorerResponse;
import org.cotato.csquiz.api.socket.dto.QuizOpenRequest;
import org.cotato.csquiz.api.socket.dto.QuizSocketRequest;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Record;
import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.repository.RecordRepository;
import org.cotato.csquiz.domain.education.repository.ScorerRepository;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.education.cache.QuizAnswerRedisRepository;
import org.cotato.csquiz.domain.education.cache.ScorerExistRedisRepository;
import org.cotato.csquiz.domain.education.cache.TicketCountRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private static final String INPUT_DELIMITER = ",";
    private final MemberService memberService;
    private final RecordRepository recordRepository;
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final ScorerRepository scorerRepository;
    private final QuizAnswerRedisRepository quizAnswerRedisRepository;
    private final TicketCountRedisRepository ticketCountRedisRepository;
    private final ScorerExistRedisRepository scorerExistRedisRepository;

    @Transactional
    public ReplyResponse replyToQuiz(ReplyRequest request) {
        Quiz findQuiz = findQuizById(request.quizId());
        checkQuizStart(findQuiz);
        Long ticketNumber = ticketCountRedisRepository.increment(findQuiz.getId());

        Member findMember = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        checkMemberAlreadyCorrect(findQuiz, findMember);
        List<String> inputs = request.inputs().stream()
                .map(String::toLowerCase)
                .map(String::trim)
                .sorted()
                .toList();

        boolean isCorrect = quizAnswerRedisRepository.isCorrect(findQuiz, inputs);

        String reply = String.join(INPUT_DELIMITER, inputs);
        Record createdRecord = Record.of(reply, isCorrect, findMember, findQuiz, ticketNumber);

        if (isCorrect && scorerExistRedisRepository.saveScorerIfIsFastest(findQuiz, ticketNumber)) {
            scorerRepository.findByQuizId(findQuiz.getId())
                    .ifPresentOrElse(
                            scorer -> {
                                scorer.updateMemberId(findMember.getId());
                                scorerRepository.save(scorer);
                            },
                            () -> createScorer(createdRecord)
                    );
            log.info("득점자 생성 : {}, 티켓번호: {}", findMember.getId(), ticketNumber);
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
        if (recordRepository.findByQuizAndMemberIdAndIsCorrect(findQuiz, findMember.getId(), true).isPresent()) {
            log.warn("이미 해당 문제에 정답 제출한 사용자입니다.");
            log.warn("문제 번호: {}, 제출한 멤버: {}", findQuiz.getNumber(), findMember.getName());
            throw new AppException(ErrorCode.ALREADY_REPLY_CORRECT);
        }
    }

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("해당 문제를 찾을 수 없습니다."));
    }

    @Transactional
    public void saveAnswers(QuizOpenRequest request) {
        scorerExistRedisRepository.saveAllScorerNone(request.educationId());
        quizAnswerRedisRepository.saveAllQuizAnswers(request.educationId());
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

        scorerRepository.findByQuizId(quiz.getId())
                .ifPresentOrElse(
                        scorer -> updateScorer(scorer, fastestRecord),
                        () -> createScorer(fastestRecord)
                );
    }

    private void checkQuizType(Quiz quiz) {
        if (quiz instanceof MultipleQuiz) {
            throw new AppException(ErrorCode.QUIZ_TYPE_NOT_MATCH);
        }
    }

    private void updateScorer(Scorer previousScorer, Record fastestRecord) {
        if (isFaster(previousScorer, fastestRecord)) {
            log.info("[득점자 변경] 새로운 티켓 번호: {}", fastestRecord.getTicketNumber());
            previousScorer.updateMemberId(fastestRecord.getMemberId());
            scorerRepository.save(previousScorer);
        }
    }

    private boolean isFaster(Scorer previousScorer, Record fastestRecord) {
        Quiz findQuiz = quizRepository.findById(previousScorer.getQuizId())
                .orElseThrow(() -> new EntityNotFoundException("이전 득점자가 맞춘 퀴즈가 존재하지 않습니다."));
        return scorerExistRedisRepository.getScorerTicketNumber(findQuiz) > fastestRecord.getTicketNumber();
    }

    private void createScorer(Record fastestRecord) {
        Scorer scorer = Scorer.of(fastestRecord.getMemberId(), fastestRecord.getQuiz());
        scorerRepository.save(scorer);
        scorerExistRedisRepository.saveScorer(fastestRecord.getQuiz(), fastestRecord.getTicketNumber());
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
        scorerExistRedisRepository.saveScorer(findQuiz, Long.MAX_VALUE);
    }
}
