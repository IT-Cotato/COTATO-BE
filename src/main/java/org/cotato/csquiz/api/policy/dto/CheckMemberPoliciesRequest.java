package org.cotato.csquiz.api.policy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CheckMemberPoliciesRequest(
        @NotEmpty(message = "체크할 정책을 입력해주세요")
        @Valid
        List<CheckPolicyRequest> policies
) {
}
