package cotato.csquiz.controller.dto.education;

import jakarta.validation.constraints.NotNull;

public record CreateEducationRequest(
        @NotNull
        String subject,
        @NotNull
        Long sessionId,
        @NotNull
        Integer educationNum
) {
}
