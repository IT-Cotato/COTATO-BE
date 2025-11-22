package org.cotato.csquiz.api.policy.dto;

import java.util.List;

public record PoliciesResponse(
	List<PolicyInfoResponse> policies
) {
}
