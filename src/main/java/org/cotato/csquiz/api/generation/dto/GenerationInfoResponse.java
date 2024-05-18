package org.cotato.csquiz.api.generation.dto;

import org.cotato.csquiz.domain.generation.entity.Generation;

public record GenerationInfoResponse(
        Long generationId,
        Integer generationNumber,
        Integer sessionCount
) {

    public static GenerationInfoResponse from(Generation generation) {
        return new GenerationInfoResponse(
                generation.getId(),
                generation.getNumber(),
                generation.getSessionCount()
        );
    }
}
