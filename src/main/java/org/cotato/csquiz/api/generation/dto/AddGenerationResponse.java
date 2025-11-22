package org.cotato.csquiz.api.generation.dto;

import org.cotato.csquiz.domain.generation.entity.Generation;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record AddGenerationResponse(
	@Schema(requiredMode = RequiredMode.REQUIRED)
	Long generationId
) {
	public static AddGenerationResponse from(Generation generation) {
		return new AddGenerationResponse(generation.getId());
	}
}
