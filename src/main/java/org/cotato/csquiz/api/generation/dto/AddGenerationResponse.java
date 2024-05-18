package org.cotato.csquiz.api.generation.dto;

import org.cotato.csquiz.domain.generation.entity.Generation;

public record AddGenerationResponse(
        Long generationId
) {
    public static AddGenerationResponse from(Generation generation) {
        return new AddGenerationResponse(generation.getId());
    }
}
