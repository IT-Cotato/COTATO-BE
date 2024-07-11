package org.cotato.csquiz.api.policy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record FindMemberPolicyResponse(
        Long memberId,
        @Schema(description = "회원이 체크하지 않은 필수 정책 목록")
        List<PolicyInfoResponse> policies
) {
    public static FindMemberPolicyResponse of(Long memberId, List<PolicyInfoResponse> unCheckedPolicies) {
        return new FindMemberPolicyResponse(memberId, unCheckedPolicies);
    }
}
