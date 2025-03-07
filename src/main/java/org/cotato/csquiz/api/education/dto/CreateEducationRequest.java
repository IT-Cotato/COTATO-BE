package org.cotato.csquiz.api.education.dto;

import jakarta.validation.constraints.NotNull;

public record CreateEducationRequest(
        @NotNull
        String subject,
        @NotNull
        Long generationId,
        @NotNull
        Integer educationNumber
) {
}
