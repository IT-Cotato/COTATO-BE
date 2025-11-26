package org.cotato.csquiz.api.record.dto;

import jakarta.validation.constraints.NotNull;

public record RegradeRequest(
	@NotNull
	Long quizId,
	@NotNull
	String newAnswer
) {
}
