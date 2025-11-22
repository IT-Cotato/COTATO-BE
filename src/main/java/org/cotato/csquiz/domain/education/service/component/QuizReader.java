package org.cotato.csquiz.domain.education.service.component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.cotato.csquiz.domain.education.cache.DiscordQuizRedisRepository;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QuizReader {

	private static final int MAX_DISCORD_QUIZ_LENGTH = 80;

	private final EducationReader educationReader;
	private final QuizRepository quizRepository;
	private final DiscordQuizRedisRepository discordQuizRedisRepository;

	@Transactional(readOnly = true)
	public Quiz getById(final Long id) {
		return quizRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("해당 퀴즈가 존재하지 않습니다."));
	}

	@Transactional(readOnly = true)
	public Quiz getRandomDiscordQuiz() {
		List<Education> finishedEducations = educationReader.getAllByStatus(EducationStatus.FINISHED);

		List<Quiz> multipleQuizzes = quizRepository.findMultipleQuizzesByEducationInAndQuestionLengthLE(
				finishedEducations, MAX_DISCORD_QUIZ_LENGTH).stream()
			.filter(quiz -> !discordQuizRedisRepository.isUsedInOneWeek(quiz.getId()))
			.toList();

		if (multipleQuizzes.isEmpty()) {
			throw new EntityNotFoundException("디스코드에 전송할 랜덤 퀴즈가 없습니다.");
		}

		ThreadLocalRandom random = ThreadLocalRandom.current();
		Quiz quiz = multipleQuizzes.get(random.nextInt(multipleQuizzes.size()));
		discordQuizRedisRepository.save(quiz.getId());

		return quiz;
	}

	@Transactional(readOnly = true)
	public List<Quiz> getAllByEducation(Education education) {
		return quizRepository.findAllByEducationId(education.getId());
	}
}
