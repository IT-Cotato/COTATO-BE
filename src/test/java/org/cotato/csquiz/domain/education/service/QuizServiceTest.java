package org.cotato.csquiz.domain.education.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cotato.csquiz.api.quiz.dto.AllQuizzesResponse;
import org.cotato.csquiz.api.quiz.dto.MultipleQuizResponse;
import org.cotato.csquiz.api.quiz.dto.ShortQuizResponse;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.ShortAnswer;
import org.cotato.csquiz.domain.education.entity.ShortQuiz;
import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;
import org.cotato.csquiz.domain.education.enums.QuizType;
import org.cotato.csquiz.domain.education.service.component.ChoiceReader;
import org.cotato.csquiz.domain.education.service.component.EducationReader;
import org.cotato.csquiz.domain.education.service.component.QuizReader;
import org.cotato.csquiz.domain.education.service.component.ShortAnswerReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private EducationReader educationReader;

    @Mock
    private QuizReader quizReader;

    @Mock
    private ChoiceReader choiceReader;

    @Mock
    private ShortAnswerReader shortAnswerReader;

    @InjectMocks
    private QuizService quizService;

    private final Long educationId = 1L;
    private Education education;

    @BeforeEach
    void setUp() {
        education = Education.builder().build();
        when(educationReader.getById(educationId)).thenReturn(education);
    }

    @Test
    @DisplayName("교육별 퀴즈 목록 조회 테스트")
    void findAllQuizzes_withMultipleAndShortQuizzes_returnsMappedResponses() {
        // given
        MultipleQuiz multipleQuiz = mock(MultipleQuiz.class);
        when(multipleQuiz.getQuizType()).thenReturn(QuizType.MULTIPLE_QUIZ);

        ShortQuiz shortQuiz = mock(ShortQuiz.class);
        when(shortQuiz.getQuizType()).thenReturn(QuizType.SHORT_QUIZ);

        List<Quiz> allQuizzes = List.of(multipleQuiz, shortQuiz);
        when(quizReader.getAllByEducation(education)).thenReturn(allQuizzes);

        // 2) choiceReader, shortAnswerReader stub
        List<Choice> dummyChoices = List.of(Choice.of(1, "1", ChoiceCorrect.ANSWER, multipleQuiz), Choice.of(2, "2", ChoiceCorrect.ANSWER, multipleQuiz));
        when(choiceReader.getChoicesByMultipleQuizzes(List.of(multipleQuiz))).thenReturn(Map.of(multipleQuiz, dummyChoices));

        List<ShortAnswer> dummyAnswers = List.of(ShortAnswer.of("answer1", shortQuiz), ShortAnswer.of("answer2", shortQuiz));
        when(shortAnswerReader.getAnswersByShortQuizzes(List.of(shortQuiz))).thenReturn(Map.of(shortQuiz, dummyAnswers));

        // when
        AllQuizzesResponse response = quizService.findAllQuizzesForEducationTeam(educationId);

        // then
        assertThat(response.getMultiples()).hasSize(1);
        MultipleQuizResponse mRes = response.getMultiples().get(0);
        assertThat(mRes.getQuestion()).isEqualTo(multipleQuiz.getQuestion());

        assertThat(response.getShortQuizzes()).hasSize(1);
        ShortQuizResponse sRes = response.getShortQuizzes().get(0);
        assertThat(sRes.getQuestion()).isEqualTo(shortQuiz.getQuestion());

        verify(quizReader).getAllByEducation(education);
        verify(choiceReader).getChoicesByMultipleQuizzes(List.of(multipleQuiz));
        verify(shortAnswerReader).getAnswersByShortQuizzes(List.of(shortQuiz));
    }

    @Test
    @DisplayName("퀴즈가 없을 때 빈 리스트를 반환하고 NPE가 발생하지 않아야 한다.")
    void findAllQuizzes_noQuizzes_returnsEmptyListsWithoutNpe() {
        // --- given ---
        when(quizReader.getAllByEducation(education)).thenReturn(Collections.emptyList());
        when(choiceReader.getChoicesByMultipleQuizzes(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());
        when(shortAnswerReader.getAnswersByShortQuizzes(Collections.emptyList()))
                .thenReturn(Collections.emptyMap());

        // --- when ---
        AllQuizzesResponse response = quizService.findAllQuizzesForEducationTeam(educationId);

        // --- then ---
        assertThat(response.getMultiples()).isEmpty();
        assertThat(response.getShortQuizzes()).isEmpty();

        verify(quizReader).getAllByEducation(education);
        verify(choiceReader).getChoicesByMultipleQuizzes(Collections.emptyList());
        verify(shortAnswerReader).getAnswersByShortQuizzes(Collections.emptyList());
    }
}
