package org.cotato.csquiz.api.generation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ChangeGenerationPeriodRequest(
	@NotNull
	Long generationId,
	@NotNull
	LocalDate startDate,
	@NotNull
	LocalDate endDate
) {
}
