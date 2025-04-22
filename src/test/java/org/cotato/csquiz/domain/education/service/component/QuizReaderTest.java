package org.cotato.csquiz.domain.education.service.component;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
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
        Optional<Quiz> result = quizReader.getRandomDiscordQuiz();

        // then
        assertTrue(result.isPresent(), "퀴즈가 반환되어야 한다");
        assertSame(quiz, result.get(), "반환된 퀴즈가 기대한 객체여야 한다");
        verify(discordQuizRedisRepository).save(1L);
    }

    @Test
    void getRandomDiscordQuiz_allQuizzesUsed_throwsExceptionAndDoesNotSave() {
        // given
        Education edu = mock(Education.class);
        List<Education> eds = List.of(edu);
        when(educationReader.getAllByStatus(EducationStatus.FINISHED))
                .thenReturn(eds);

        Quiz quiz = new Quiz(1, "another question", null, edu, 5);
        ReflectionTestUtils.setField(quiz, "id", 2L);

        when(quizRepository.findMultipleQuizzesByEducationInAndQuestionLengthLE(eds, 80))
                .thenReturn(List.of(quiz));
        when(discordQuizRedisRepository.isUsedInOneWeek(2L))
                .thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> quizReader.getRandomDiscordQuiz(),
                "사용된 퀴즈만 남을 경우 nextInt(0) 에러가 발생해야 한다");
        verify(discordQuizRedisRepository, never()).save(anyLong());
    }
}
