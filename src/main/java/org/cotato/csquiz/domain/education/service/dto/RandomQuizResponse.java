package org.cotato.csquiz.domain.education.service.dto;

import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.QuizType;

public record RandomQuizResponse(
        Long id,
        int number,
        QuizType quizType,
        String question,
        String imageUrl
) {
    public static RandomQuizResponse from(Quiz quiz) {
        return new RandomQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuizType(),
                quiz.getQuestion(),
                (quiz.getS3Info() != null) ? quiz.getS3Info().getUrl() : null
        );
    }
}
