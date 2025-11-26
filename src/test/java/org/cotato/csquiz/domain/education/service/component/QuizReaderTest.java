package org.cotato.csquiz.domain.education.service.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.cotato.csquiz.domain.education.cache.DiscordQuizRedisRepository;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class QuizReaderTest {

	@Mock
	private EducationReader educationReader;

	@Mock
	private QuizRepository quizRepository;

	@Mock
	private DiscordQuizRedisRepository discordQuizRedisRepository;

	@InjectMocks
	private QuizReader quizReader;

	@Test
	void getRandomDiscordQuiz_singleQuizAvailable_returnsThatQuiz() {
		// given
		Education edu = mock(Education.class);
		List<Education> eds = List.of(edu);
		when(educationReader.getAllByStatus(EducationStatus.FINISHED))
			.thenReturn(eds);

		Quiz quiz = new Quiz(1, "short question", null, edu, 5);
		ReflectionTestUtils.setField(quiz, "id", 1L);

		when(quizRepository.findMultipleQuizzesByEducationInAndQuestionLengthLE(eds, 80))
			.thenReturn(List.of(quiz));
		when(discordQuizRedisRepository.isUsedInOneWeek(1L))
			.thenReturn(false);

		// when
		Quiz result = quizReader.getRandomDiscordQuiz();

		// then
		assertSame(quiz, result, "반환된 퀴즈가 기대한 객체여야 한다");
		verify(discordQuizRedisRepository).save(1L);
	}

	@Test
	void getRandomDiscordQuiz_allQuizzesUsed_throwsIllegalArgumentException() {
		// given
		Education edu = mock(Education.class);
		List<Education> eds = List.of(edu);
		when(educationReader.getAllByStatus(EducationStatus.FINISHED))
			.thenReturn(eds);

		Quiz usedQuiz = new Quiz(2, "used question", null, edu, 3);
		ReflectionTestUtils.setField(usedQuiz, "id", 2L);

		when(quizRepository.findMultipleQuizzesByEducationInAndQuestionLengthLE(eds, 80))
			.thenReturn(List.of(usedQuiz));
		when(discordQuizRedisRepository.isUsedInOneWeek(2L))
			.thenReturn(true);

		// when & then
		EntityNotFoundException ex = assertThrows(
			EntityNotFoundException.class,
			() -> quizReader.getRandomDiscordQuiz()
		);
		assertEquals("디스코드에 전송할 랜덤 퀴즈가 없습니다.", ex.getMessage(), "사용 가능한 퀴즈가 없을 때 지정한 메시지의 예외가 발생해야 한다");
		verify(discordQuizRedisRepository, never()).save(anyLong());
	}

	@Test
	void getRandomDiscordQuiz_noQuizzesAtAll_throwsEntityNotFoundException() {
		// given
		when(educationReader.getAllByStatus(EducationStatus.FINISHED))
			.thenReturn(List.of());
		when(quizRepository.findMultipleQuizzesByEducationInAndQuestionLengthLE(anyList(), eq(80)))
			.thenReturn(List.of());

		// when & then
		EntityNotFoundException ex = assertThrows(
			EntityNotFoundException.class,
			() -> quizReader.getRandomDiscordQuiz()
		);
		assertEquals("디스코드에 전송할 랜덤 퀴즈가 없습니다.", ex.getMessage());
		verify(discordQuizRedisRepository, never()).save(anyLong());
	}
}
