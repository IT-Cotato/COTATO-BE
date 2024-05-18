package cotato.csquiz.controller.dto.record;

import jakarta.validation.constraints.NotNull;

public record RegradeRequest(
        @NotNull
        Long quizId,
        @NotNull
        String newAnswer
) {
}
