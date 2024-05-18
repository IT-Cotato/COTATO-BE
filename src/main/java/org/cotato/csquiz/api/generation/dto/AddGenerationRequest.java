package org.cotato.csquiz.api.generation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AddGenerationRequest(
        @NotNull
        Integer generationNumber,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotNull
        Integer sessionCount
) {
}
