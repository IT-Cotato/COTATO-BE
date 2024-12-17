package org.cotato.csquiz.api.quiz.dto;

import java.util.List;
import org.cotato.csquiz.domain.education.entity.RandomQuiz;

public record RandomTutorialQuizResponse(
        Long id,
        String question,
        String imageUrl,
        List<String> choices
) {
    public static RandomTutorialQuizResponse from(final RandomQuiz randomQuiz) {
        return new RandomTutorialQuizResponse(
                randomQuiz.getId(),
                randomQuiz.getQuestion(),
                randomQuiz.getImageUrl(),
                randomQuiz.getChoices()
        );
    }
}
