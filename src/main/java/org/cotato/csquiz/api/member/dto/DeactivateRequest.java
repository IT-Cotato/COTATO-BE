package org.cotato.csquiz.api.member.dto;

import java.util.List;

import org.cotato.csquiz.api.policy.dto.CheckPolicyRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record DeactivateRequest(
	@NotNull
	@Email
	String email,
	@NotNull
	String password,

	@NotNull @Valid
	List<CheckPolicyRequest> checkedPolicies
) {
}
