package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.entity.ShortAnswer;

public record ShortAnswerResponse(
        String answer
) {
    public static ShortAnswerResponse from(ShortAnswer shortAnswer) {
        return new ShortAnswerResponse(
                shortAnswer.getContent()
        );
    }
}
