package org.cotato.csquiz.api.generation.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeRecruitingStatusRequest(
	@NotNull
	Long generationId,
	@NotNull
	boolean statement
) {
}
