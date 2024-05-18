package org.cotato.csquiz.api.generation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ChangeGenerationPeriodRequest(
        @NotNull
        Long generationId,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
}
