package org.cotato.csquiz.api.policy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.cotato.csquiz.domain.auth.entity.Member;

public record FindMemberPolicyResponse(
        Long memberId,
        @Schema(description = "회원이 체크하지 않은 필수 정책 목록")
        List<PolicyInfoResponse> essentialPolicies,
        @Schema(description = "회원이 체크하지 않은 선택 정책 목록")
        List<PolicyInfoResponse> optionalPolicies
) {
    public static FindMemberPolicyResponse of(Member member, List<PolicyInfoResponse> essentialPolicies,
                                              List<PolicyInfoResponse> optionalPolicies) {
        return new FindMemberPolicyResponse(member.getId(), essentialPolicies, optionalPolicies);
    }
}
