package org.cotato.csquiz.api.policy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CheckMemberPoliciesRequest(
        @NotNull(message = "체크할 회원의 id는 필수 입니다.")
        Long memberId,
        @NotEmpty(message = "체크할 정책을 입력해주세요")
        @Valid
        List<CheckPolicyRequest> policies
) {
}
