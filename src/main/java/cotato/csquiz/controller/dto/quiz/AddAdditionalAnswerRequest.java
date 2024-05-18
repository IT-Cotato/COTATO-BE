package cotato.csquiz.controller.dto.quiz;

import jakarta.validation.constraints.NotNull;

public record AddAdditionalAnswerRequest(
        @NotNull
        Long quizId,
        @NotNull
        String answer
) {
}
