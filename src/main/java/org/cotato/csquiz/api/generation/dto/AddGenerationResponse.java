package org.cotato.csquiz.api.generation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.generation.entity.Generation;

public record AddGenerationResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long generationId
) {
    public static AddGenerationResponse from(Generation generation) {
        return new AddGenerationResponse(generation.getId());
    }
}
