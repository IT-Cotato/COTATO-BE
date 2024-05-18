package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.QuizType;
import java.util.List;

public record QuizInfoInCsQuizResponse(
        Long quizId,
        QuizType quizType,
        Integer quizNumber,
        String question,
        List<String> answer
) {
    public static QuizInfoInCsQuizResponse from(Quiz quiz, List<String> answer) {
        return new QuizInfoInCsQuizResponse(
                quiz.getId(),
                quiz.getQuizType(),
                quiz.getNumber(),
                quiz.getQuestion(),
                answer
        );
    }
}
