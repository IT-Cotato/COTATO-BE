package org.cotato.csquiz.api.generation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDate;
import org.cotato.csquiz.domain.generation.entity.Generation;

public record GenerationInfoResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long generationId,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Integer generationNumber,
        Integer sessionCount,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        LocalDate startDate,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        LocalDate endDate
) {

    public static GenerationInfoResponse from(Generation generation) {
        return new GenerationInfoResponse(
                generation.getId(),
                generation.getNumber(),
                generation.getSessionCount(),
                generation.getPeriod().getStartDate(),
                generation.getPeriod().getEndDate()
        );
    }
}
