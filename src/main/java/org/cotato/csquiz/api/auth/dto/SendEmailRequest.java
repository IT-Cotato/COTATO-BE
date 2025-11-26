package org.cotato.csquiz.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SendEmailRequest(
	@Email
	@NotNull
	String email
) {
}
