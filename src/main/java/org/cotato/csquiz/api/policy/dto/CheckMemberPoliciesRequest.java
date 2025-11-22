package org.cotato.csquiz.api.policy.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CheckMemberPoliciesRequest(
	@NotEmpty(message = "체크할 정책을 입력해주세요")
	@Valid
	List<CheckPolicyRequest> policies
) {
}
