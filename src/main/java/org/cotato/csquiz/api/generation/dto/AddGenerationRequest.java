package org.cotato.csquiz.api.generation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AddGenerationRequest(
	@NotNull
	Integer generationNumber,
	@NotNull
	LocalDate startDate,
	@NotNull
	LocalDate endDate
) {
}
