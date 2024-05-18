package cotato.csquiz.controller.dto.education;

import jakarta.validation.constraints.NotNull;

public record UpdateEducationRequest(
        @NotNull
        Long educationId,
        @NotNull
        String newSubject,
        @NotNull
        Integer newNumber
) {
}
