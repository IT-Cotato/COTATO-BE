package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.quiz.dto.AddAdditionalAnswerRequest;
import org.cotato.csquiz.api.quiz.dto.AllQuizzesInCsQuizResponse;
import org.cotato.csquiz.api.quiz.dto.AllQuizzesResponse;
import org.cotato.csquiz.api.quiz.dto.ChoiceResponse;
import org.cotato.csquiz.api.quiz.dto.CreateChoiceRequest;
import org.cotato.csquiz.api.quiz.dto.CreateMultipleQuizRequest;
import org.cotato.csquiz.api.quiz.dto.CreateQuizzesRequest;
import org.cotato.csquiz.api.quiz.dto.CreateShortAnswerRequest;
import org.cotato.csquiz.api.quiz.dto.CreateShortQuizRequest;
import org.cotato.csquiz.api.quiz.dto.CsAdminQuizResponse;
import org.cotato.csquiz.api.quiz.dto.FindMultipleQuizResponse;
import org.cotato.csquiz.api.quiz.dto.MultipleQuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizInfoInCsQuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizResultInfo;
import org.cotato.csquiz.api.quiz.dto.ShortAnswerResponse;
import org.cotato.csquiz.api.quiz.dto.ShortQuizResponse;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.education.entity.ShortAnswer;
import org.cotato.csquiz.domain.education.entity.ShortQuiz;
import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;
import org.cotato.csquiz.domain.education.repository.ChoiceRepository;
import org.cotato.csquiz.domain.education.repository.EducationRepository;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.repository.ScorerRepository;
import org.cotato.csquiz.domain.education.repository.ShortAnswerRepository;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.S3.S3Uploader;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private static final String QUIZ_BUCKET_DIRECTORY = "quiz";
    private static final int RANDOM_DELAY_TIME_BOUNDARY = 7;
    private final MemberService memberService;
    private final EducationRepository educationRepository;
    private final QuizRepository quizRepository;
    private final ScorerRepository scorerRepository;
    private final ShortAnswerRepository shortAnswerRepository;
    private final ChoiceRepository choiceRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void createQuizzes(Long educationId, CreateQuizzesRequest request) throws ImageException {
        Education findEducation = findEducationById(educationId);
        checkQuizBefore(findEducation);

        log.info("등록할 교육 회차 : {}회차", findEducation.getNumber());
        List<Integer> quizNumbers = Stream.concat(
                request.getMultiples().stream()
                        .map(CreateMultipleQuizRequest::getNumber),
                request.getShortQuizzes().stream()
                        .map(CreateShortQuizRequest::getNumber)
        ).toList();

        checkQuizNumbersDistinct(quizNumbers);

        deleteAllQuizByEducation(educationId);

        for (CreateShortQuizRequest shortQuizRequest : request.getShortQuizzes()) {
            createShortQuiz(findEducation, shortQuizRequest);
        }
        for (CreateMultipleQuizRequest multipleQuizRequest : request.getMultiples()) {
            createMultipleQuiz(findEducation, multipleQuizRequest);
        }
    }

    private void checkQuizBefore(Education findEducation) {
        if (EducationStatus.BEFORE != findEducation.getStatus()) {
            throw new AppException(ErrorCode.EDUCATION_STATUS_NOT_BEFORE);
        }
    }

    private void checkQuizNumbersDistinct(final List<Integer> numbers) {
        Set<Integer> distinctNumbers = new HashSet<>(numbers);
        if (numbers.size() != distinctNumbers.size()) {
            throw new AppException(ErrorCode.QUIZ_NUMBER_DUPLICATED);
        }
    }

    private void deleteAllQuizByEducation(Long educationId) {
        List<Quiz> quizList = quizRepository.findAllByEducationId(educationId);
        List<Long> quizIds = quizList.stream()
                .map(Quiz::getId)
                .toList();

        getNotNullS3Infos(quizList).forEach(s3Uploader::deleteFile);

        choiceRepository.deleteAllByQuizIdsInQuery(quizIds);
        shortAnswerRepository.deleteAllByQuizIdsInQuery(quizIds);
        quizRepository.deleteAllByQuizIdsInQuery(quizIds);
    }

    private static List<S3Info> getNotNullS3Infos(List<Quiz> quizList) {
        return quizList.stream()
                .map(Quiz::getS3Info)
                .filter(Objects::nonNull)
                .toList();
    }

    private void createShortQuiz(Education findEducation, CreateShortQuizRequest request)
            throws ImageException {
        S3Info s3Info = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            s3Info = s3Uploader.uploadFiles(request.getImage(), QUIZ_BUCKET_DIRECTORY);
        }

        ShortQuiz createdShortQuiz = ShortQuiz.builder()
                .education(findEducation)
                .question(request.getQuestion())
                .number(request.getNumber())
                .s3Info(s3Info)
                .appearSecond(generateRandomTime())
                .build();
        log.info("주관식 문제 생성 완료: 사진 정보 {}", s3Info);
        quizRepository.save(createdShortQuiz);

        List<ShortAnswer> shortAnswers = request.getShortAnswers().stream()
                .map(CreateShortAnswerRequest::getAnswer)
                .map(String::toLowerCase)
                .map(String::trim)
                .map(answer -> ShortAnswer.of(answer, createdShortQuiz))
                .toList();
        shortAnswerRepository.saveAll(shortAnswers);

        log.info("주관식 정답 생성 완료: {}개", shortAnswers.size());
    }

    private void createMultipleQuiz(Education findEducation, CreateMultipleQuizRequest request)
            throws ImageException {
        S3Info s3Info = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            s3Info = s3Uploader.uploadFiles(request.getImage(), QUIZ_BUCKET_DIRECTORY);
        }

        MultipleQuiz createdMultipleQuiz = MultipleQuiz.builder()
                .education(findEducation)
                .number(request.getNumber())
                .question(request.getQuestion())
                .s3Info(s3Info)
                .appearSecond(generateRandomTime())
                .build();

        log.info("객관식 문제 생성, 사진 정보 {}", s3Info);
        quizRepository.save(createdMultipleQuiz);

        List<Integer> choiceNumbers = request.getChoices().stream().map(CreateChoiceRequest::getNumber).toList();
        validateChoiceNumbers(choiceNumbers);

        List<Choice> choices = request.getChoices().stream()
                .map(requestDto -> Choice.of(requestDto, createdMultipleQuiz))
                .toList();
        choiceRepository.saveAll(choices);
        log.info("객관식 선지 생성 : {}개", choices.size());
    }

    private void validateChoiceNumbers(final List<Integer> choiceNumbers) {
        Set<Integer> distinctNumbers = new HashSet<>(choiceNumbers);
        if (distinctNumbers.size() != choiceNumbers.size()) {
            throw new AppException(ErrorCode.CHOICE_NUMBER_DUPLICATED);
        }
    }

    @Transactional
    public List<QuizResultInfo> createQuizResults(Long educationId) {
        List<Quiz> quizzes = quizRepository.findAllByEducationId(educationId);

        return quizzes.stream()
                .map(this::createQuizResultInfo)
                .toList();
    }

    private QuizResultInfo createQuizResultInfo(Quiz quiz) {
        Optional<Scorer> mayBeScorer = scorerRepository.findByQuizId(quiz.getId());

        if (mayBeScorer.isPresent()) {
            Member member = memberService.findById(mayBeScorer.get().getMemberId());

            return QuizResultInfo.of(quiz, member, memberService.findBackFourNumber(member));
        }
        return QuizResultInfo.noScorer(quiz);
    }

    @Transactional
    public AllQuizzesResponse findAllQuizzesForEducationTeam(Long educationId) {
        List<Quiz> quizzes = quizRepository.findAllByEducationId(educationId);

        List<MultipleQuizResponse> multiples = quizzes.stream()
                .filter(quiz -> quiz instanceof MultipleQuiz)
                .map(this::toMultipleQuizResponse)
                .toList();
        List<ShortQuizResponse> shortQuizzes = quizzes.stream()
                .filter(quiz -> quiz instanceof ShortQuiz)
                .map(this::toShortQuizResponse)
                .toList();

        return AllQuizzesResponse.builder()
                .multiples(multiples)
                .shortQuizzes(shortQuizzes)
                .build();
    }

    private MultipleQuizResponse toMultipleQuizResponse(Quiz quiz) {
        List<ChoiceResponse> choiceResponses = choiceRepository.findAllByMultipleQuiz((MultipleQuiz) quiz).stream()
                .map(ChoiceResponse::forEducation)
                .toList();
        return MultipleQuizResponse.from(quiz, choiceResponses);
    }

    private ShortQuizResponse toShortQuizResponse(Quiz quiz) {
        List<ShortAnswerResponse> shortAnswerResponses = shortAnswerRepository.findAllByShortQuiz((ShortQuiz) quiz)
                .stream()
                .map(ShortAnswerResponse::from)
                .toList();
        return ShortQuizResponse.from(quiz, shortAnswerResponses);
    }

    @Transactional
    public AllQuizzesInCsQuizResponse findAllQuizzesForAdminCsQuiz(Long educationId) {
        List<Quiz> quizzes = quizRepository.findAllByEducationId(educationId);

        List<CsAdminQuizResponse> responses = quizzes.stream()
                .map(CsAdminQuizResponse::from)
                .toList();

        return AllQuizzesInCsQuizResponse.from(responses);
    }

    @Transactional
    public QuizResponse findOneQuizForMember(Long quizId) {
        Quiz findQuiz = findQuizById(quizId);

        if (findQuiz instanceof MultipleQuiz) {
            List<ChoiceResponse> choiceResponses = choiceRepository.findAllByMultipleQuiz((MultipleQuiz) findQuiz)
                    .stream()
                    .map(ChoiceResponse::forMember)
                    .toList();

            return FindMultipleQuizResponse.from(findQuiz, choiceResponses);
        }

        return QuizResponse.from((ShortQuiz) findQuiz);
    }

    @Transactional
    public QuizInfoInCsQuizResponse findQuizForAdminCsQuiz(Long quizId) {
        Quiz quiz = findQuizById(quizId);
        List<String> answers = getAnswerList(quiz);

        return QuizInfoInCsQuizResponse.from(quiz, answers);
    }

    private List<String> getAnswerList(Quiz quiz) {
        if (quiz instanceof ShortQuiz) {
            return getShortQuizAnswer(quiz);
        }
        return getMultipleQuizAnswer(quiz);
    }

    private List<String> getShortQuizAnswer(Quiz quiz) {
        List<ShortAnswer> shortAnswers = shortAnswerRepository.findAllByShortQuiz((ShortQuiz) quiz);
        return shortAnswers.stream()
                .map(ShortAnswer::getContent)
                .toList();
    }

    private List<String> getMultipleQuizAnswer(Quiz quiz) {
        List<Choice> choices = choiceRepository.findAllByMultipleQuiz((MultipleQuiz) quiz);
        return choices.stream()
                .filter(choice -> choice.getIsCorrect() == ChoiceCorrect.ANSWER)
                .map(choice -> String.valueOf(choice.getChoiceNumber()))
                .toList();
    }

    @Transactional
    public void addAdditionalAnswer(AddAdditionalAnswerRequest request) {
        Quiz quiz = findQuizById(request.quizId());
        if (quiz instanceof ShortQuiz) {
            addShortAnswer((ShortQuiz) quiz, request.answer());
        }
        if (quiz instanceof MultipleQuiz) {
            addCorrectChoice((MultipleQuiz) quiz, request.answer());
        }
    }

    private void addShortAnswer(ShortQuiz shortQuiz, String answer) {
        checkAnswerAlreadyExist(shortQuiz, answer);

        String cleanedAnswer = answer.toLowerCase()
                .trim();
        ShortAnswer shortAnswer = ShortAnswer.of(cleanedAnswer, shortQuiz);

        shortAnswerRepository.save(shortAnswer);
    }

    private void checkAnswerAlreadyExist(ShortQuiz shortQuiz, String answer) {
        shortAnswerRepository.findByShortQuizAndContent(shortQuiz, answer)
                .ifPresent(existingAnswer -> {
                    throw new AppException(ErrorCode.CONTENT_IS_ALREADY_ANSWER);
                });
    }

    private void addCorrectChoice(MultipleQuiz multipleQuiz, String answer) {
        try {
            int choiceNumber = Integer.parseInt(answer);
            Choice choice = choiceRepository.findByMultipleQuizAndChoiceNumber(multipleQuiz, choiceNumber)
                    .orElseThrow(() -> new EntityNotFoundException("해당 번호의 선지를 찾을 수 없습니다."));
            choice.updateCorrect(ChoiceCorrect.ANSWER);
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.INVALID_ANSWER);
        }
    }

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈를 찾을 수 없습니다."));
    }

    private Education findEducationById(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육 id:" + educationId + " 찾다가 에러 발생했습니다."));
    }

    private int generateRandomTime() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(QuizService.RANDOM_DELAY_TIME_BOUNDARY);
    }
}
