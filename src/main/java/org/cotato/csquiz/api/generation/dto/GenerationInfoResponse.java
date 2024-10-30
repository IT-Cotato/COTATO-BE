package org.cotato.csquiz.api.generation.dto;

import java.time.LocalDate;
import org.cotato.csquiz.domain.generation.entity.Generation;

public record GenerationInfoResponse(
        Long generationId,
        Integer generationNumber,
        Integer sessionCount,
        LocalDate startDate,
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
