package org.cotato.csquiz.api.quiz.dto;

import java.util.List;
import org.cotato.csquiz.domain.education.embedded.Choices;
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
                randomQuiz.getImage() != null ? randomQuiz.getImage().getUrl() : null,
                buildChoiceList(randomQuiz.getChoices())
        );
    }

    private static List<String> buildChoiceList(Choices choices) {
        return List.of(choices.getChoice1(),
                choices.getChoice2(),
                choices.getChoice3(),
                choices.getChoice4());
    }
}
