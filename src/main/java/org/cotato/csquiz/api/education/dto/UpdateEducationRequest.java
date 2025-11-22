package org.cotato.csquiz.api.education.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateEducationRequest(
	@NotNull
	Long educationId,
	@NotNull
	String newSubject,
	@NotNull
	Integer newNumber
) {
}
