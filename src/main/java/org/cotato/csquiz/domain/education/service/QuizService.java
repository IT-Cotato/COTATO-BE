package org.cotato.csquiz.domain.education.service;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cotato.csquiz.api.quiz.dto.AddAdditionalAnswerRequest;
import org.cotato.csquiz.api.quiz.dto.AllQuizzesInCsQuizResponse;
import org.cotato.csquiz.api.quiz.dto.AllQuizzesResponse;
import org.cotato.csquiz.api.quiz.dto.ChoiceResponse;
import org.cotato.csquiz.api.quiz.dto.CreateChoiceRequest;
import org.cotato.csquiz.api.quiz.dto.CreateMultipleQuizRequest;
import org.cotato.csquiz.api.quiz.dto.CreateShortAnswerRequest;
import org.cotato.csquiz.api.quiz.dto.CreateShortQuizRequest;
import org.cotato.csquiz.api.quiz.dto.CsAdminQuizResponse;
import org.cotato.csquiz.api.quiz.dto.FindMultipleQuizResponse;
import org.cotato.csquiz.api.quiz.dto.MultipleQuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizInfoInCsQuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizResponse;
import org.cotato.csquiz.api.quiz.dto.QuizResultInfo;
import org.cotato.csquiz.api.quiz.dto.ShortQuizResponse;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.education.cache.QuizAnswerRedisRepository;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.education.entity.ShortAnswer;
import org.cotato.csquiz.domain.education.entity.ShortQuiz;
import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.domain.education.enums.QuizType;
import org.cotato.csquiz.domain.education.repository.ChoiceRepository;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.repository.ScorerRepository;
import org.cotato.csquiz.domain.education.repository.ShortAnswerRepository;
import org.cotato.csquiz.domain.education.service.component.ChoiceReader;
import org.cotato.csquiz.domain.education.service.component.EducationReader;
import org.cotato.csquiz.domain.education.service.component.QuizReader;
import org.cotato.csquiz.domain.education.service.component.ShortAnswerReader;
import org.cotato.csquiz.domain.education.util.AnswerUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

	private static final String QUIZ_BUCKET_DIRECTORY = "quiz";
	private static final int RANDOM_DELAY_TIME_BOUNDARY = 7;
	private final MemberService memberService;
	private final EducationReader educationReader;
	private final QuizReader quizReader;
	private final QuizRepository quizRepository;
	private final ScorerRepository scorerRepository;
	private final ShortAnswerRepository shortAnswerRepository;
	private final ShortAnswerReader shortAnswerReader;
	private final ChoiceRepository choiceRepository;
	private final ChoiceReader choiceReader;
	private final QuizAnswerRedisRepository quizAnswerRedisRepository;
	private final S3Uploader s3Uploader;

	@Transactional
	public void createQuizzes(final Long educationId,
		final List<CreateMultipleQuizRequest> multipleQuizRequests,
		final List<CreateShortQuizRequest> shortQuizRequests) throws ImageException {
		Education education = educationReader.getById(educationId);
		checkQuizBefore(education);

		log.info("등록할 교육 회차 : {}회차", education.getNumber());
		List<Integer> quizNumbers = Stream.concat(
			multipleQuizRequests.stream()
				.map(CreateMultipleQuizRequest::getNumber),
			shortQuizRequests.stream()
				.map(CreateShortQuizRequest::getNumber)
		).toList();

		checkQuizNumbersDistinct(quizNumbers);

		deleteAllQuizByEducation(education);

		for (CreateShortQuizRequest shortQuizRequest : shortQuizRequests) {
			createShortQuiz(education, shortQuizRequest);
		}
		for (CreateMultipleQuizRequest multipleQuizRequest : multipleQuizRequests) {
			createMultipleQuiz(education, multipleQuizRequest);
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

	private void deleteAllQuizByEducation(Education education) {
		List<Quiz> quizzes = quizReader.getAllByEducation(education);
		List<Long> quizIds = quizzes.stream()
			.map(Quiz::getId)
			.toList();

		List<S3Info> s3Infos = quizzes.stream()
			.map(Quiz::getS3Info)
			.filter(Objects::nonNull)
			.toList();
		s3Infos.forEach(s3Uploader::deleteFile);

		choiceRepository.deleteAllByQuizIdsInQuery(quizIds);
		shortAnswerRepository.deleteAllByQuizIdsInQuery(quizIds);
		quizRepository.deleteAllByQuizIdsInQuery(quizIds);
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
			.map(AnswerUtil::processAnswer)
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
			.map(requestDto -> Choice.of(requestDto.getNumber(), requestDto.getContent(), requestDto.getIsAnswer(),
				createdMultipleQuiz))
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
		Education education = educationReader.getById(educationId);
		return quizReader.getAllByEducation(education).stream()
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

	@Transactional(readOnly = true)
	public AllQuizzesResponse findAllQuizzesForEducationTeam(Long educationId) {
		Education education = educationReader.getById(educationId);

		Map<QuizType, List<Quiz>> quizzesByType = quizReader.getAllByEducation(education).stream()
			.collect(
				Collectors.groupingBy(Quiz::getQuizType, () -> new EnumMap<>(QuizType.class), Collectors.toList()));

		List<MultipleQuiz> multipleQuizzes = quizzesByType.getOrDefault(QuizType.MULTIPLE_QUIZ, List.of())
			.stream()
			.map(quiz -> (MultipleQuiz)quiz)
			.toList();
		List<MultipleQuizResponse> multipleQuizResponses = choiceReader.getChoicesByMultipleQuizzes(multipleQuizzes)
			.entrySet()
			.stream()
			.map(entry -> MultipleQuizResponse.of(entry.getKey(), entry.getValue()))
			.toList();

		List<ShortQuiz> shortQuizzes = quizzesByType.getOrDefault(QuizType.SHORT_QUIZ, List.of())
			.stream()
			.map(quiz -> (ShortQuiz)quiz)
			.toList();
		List<ShortQuizResponse> shortQuizResponses = shortAnswerReader.getAnswersByShortQuizzes(shortQuizzes)
			.entrySet()
			.stream()
			.map(entry -> ShortQuizResponse.of(entry.getKey(), entry.getValue()))
			.toList();

		return AllQuizzesResponse.builder()
			.multiples(multipleQuizResponses)
			.shortQuizzes(shortQuizResponses)
			.build();
	}

	@Transactional(readOnly = true)
	public AllQuizzesInCsQuizResponse findAllQuizzesForAdminCsQuiz(final Long educationId) {
		Education education = educationReader.getById(educationId);

		List<CsAdminQuizResponse> responses = quizReader.getAllByEducation(education).stream()
			.map(CsAdminQuizResponse::from)
			.toList();

		return AllQuizzesInCsQuizResponse.from(responses);
	}

	@Transactional
	public QuizResponse getQuizById(final Long quizId) {
		Quiz quiz = quizReader.getById(quizId);

		if (quiz instanceof MultipleQuiz) {
			List<ChoiceResponse> choiceResponses = choiceRepository.findAllByMultipleQuiz((MultipleQuiz)quiz)
				.stream()
				.map(ChoiceResponse::forMember)
				.toList();

			return FindMultipleQuizResponse.from(quiz, choiceResponses);
		}

		return QuizResponse.from((ShortQuiz)quiz);
	}

	@Transactional
	public QuizInfoInCsQuizResponse findQuizForAdminCsQuiz(Long quizId) {
		Quiz quiz = quizReader.getById(quizId);
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
		List<ShortAnswer> shortAnswers = shortAnswerRepository.findAllByShortQuiz((ShortQuiz)quiz);
		return shortAnswers.stream()
			.map(ShortAnswer::getContent)
			.toList();
	}

	private List<String> getMultipleQuizAnswer(Quiz quiz) {
		List<Choice> choices = choiceRepository.findAllByMultipleQuiz((MultipleQuiz)quiz);
		return choices.stream()
			.filter(choice -> choice.getIsCorrect() == ChoiceCorrect.ANSWER)
			.map(choice -> String.valueOf(choice.getChoiceNumber()))
			.toList();
	}

	@Transactional
	public void addAdditionalAnswer(AddAdditionalAnswerRequest request) {
		Quiz quiz = quizReader.getById(request.quizId());
		String processedAnswer = AnswerUtil.processAnswer(request.answer());
		if (quiz instanceof ShortQuiz) {
			addShortAnswer((ShortQuiz)quiz, processedAnswer);
		}
		if (quiz instanceof MultipleQuiz) {
			addCorrectChoice((MultipleQuiz)quiz, processedAnswer);
		}

		quizAnswerRedisRepository.saveAdditionalQuizAnswer(quiz, processedAnswer);
	}

	private void addShortAnswer(ShortQuiz shortQuiz, String answer) {
		checkAlreadyAnswerExist(shortQuiz, answer);

		ShortAnswer shortAnswer = ShortAnswer.of(answer, shortQuiz);
		shortAnswerRepository.save(shortAnswer);
	}

	private void checkAlreadyAnswerExist(ShortQuiz shortQuiz, String answer) {
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

	private int generateRandomTime() {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		return random.nextInt(QuizService.RANDOM_DELAY_TIME_BOUNDARY);
	}
}
