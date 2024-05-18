package cotato.csquiz.service;

import cotato.csquiz.controller.dto.quiz.AddAdditionalAnswerRequest;
import cotato.csquiz.controller.dto.quiz.AllQuizzesInCsQuizResponse;
import cotato.csquiz.controller.dto.quiz.AllQuizzesResponse;
import cotato.csquiz.controller.dto.quiz.ChoiceResponse;
import cotato.csquiz.controller.dto.quiz.CreateChoiceRequest;
import cotato.csquiz.controller.dto.quiz.CreateMultipleQuizRequest;
import cotato.csquiz.controller.dto.quiz.CreateQuizzesRequest;
import cotato.csquiz.controller.dto.quiz.CreateShortAnswerRequest;
import cotato.csquiz.controller.dto.quiz.CreateShortQuizRequest;
import cotato.csquiz.controller.dto.quiz.CsAdminQuizResponse;
import cotato.csquiz.controller.dto.quiz.FindMultipleQuizResponse;
import cotato.csquiz.controller.dto.quiz.MultipleQuizResponse;
import cotato.csquiz.controller.dto.quiz.QuizInfoInCsQuizResponse;
import cotato.csquiz.controller.dto.quiz.QuizResponse;
import cotato.csquiz.controller.dto.quiz.QuizResultInfo;
import cotato.csquiz.controller.dto.quiz.ShortAnswerResponse;
import cotato.csquiz.controller.dto.quiz.ShortQuizResponse;
import cotato.csquiz.domain.entity.Choice;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.MultipleQuiz;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.domain.entity.ShortAnswer;
import cotato.csquiz.domain.entity.ShortQuiz;
import cotato.csquiz.domain.enums.ChoiceCorrect;
import cotato.csquiz.domain.enums.EducationStatus;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.ChoiceRepository;
import cotato.csquiz.repository.EducationRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ScorerRepository;
import cotato.csquiz.repository.ShortAnswerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        List<Long> quizIds = quizRepository.findAllByEducationId(educationId).stream()
                .map(Quiz::getId)
                .toList();

        choiceRepository.deleteAllByQuizIdsInQuery(quizIds);
        shortAnswerRepository.deleteAllByQuizIdsInQuery(quizIds);
        quizRepository.deleteAllByQuizIdsInQuery(quizIds);
    }

    private void createShortQuiz(Education findEducation, CreateShortQuizRequest request)
            throws ImageException {
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = s3Uploader.uploadFiles(request.getImage(), QUIZ_BUCKET_DIRECTORY);
        }

        ShortQuiz createdShortQuiz = ShortQuiz.builder()
                .education(findEducation)
                .question(request.getQuestion())
                .number(request.getNumber())
                .photoUrl(imageUrl)
                .appearSecond(generateRandomTime())
                .build();
        log.info("주관식 문제 생성 완료: 사진 url {}", imageUrl);
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
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = s3Uploader.uploadFiles(request.getImage(), QUIZ_BUCKET_DIRECTORY);
        }

        MultipleQuiz createdMultipleQuiz = MultipleQuiz.builder()
                .education(findEducation)
                .number(request.getNumber())
                .question(request.getQuestion())
                .photoUrl(imageUrl)
                .appearSecond(generateRandomTime())
                .build();

        log.info("객관식 문제 생성, 사진 url {}", imageUrl);
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
