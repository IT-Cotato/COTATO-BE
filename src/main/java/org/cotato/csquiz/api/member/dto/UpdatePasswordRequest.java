package org.cotato.csquiz.api.member.dto;

import org.cotato.csquiz.common.validator.Password;

import jakarta.validation.constraints.NotNull;

public record UpdatePasswordRequest(
	@NotNull
	@Password
	String password
) {
}
