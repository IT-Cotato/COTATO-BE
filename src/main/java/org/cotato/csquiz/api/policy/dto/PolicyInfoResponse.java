package org.cotato.csquiz.api.policy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.cotato.csquiz.domain.auth.entity.Policy;
import org.cotato.csquiz.domain.auth.enums.PolicyType;

public record PolicyInfoResponse(
        @Schema(description = "정책 PK")
        Long policyId,
        @Schema(description = "필수 동의 항목 여부")
        PolicyType type,
        @Schema(description = "정책 타이틀")
        String title,
        @Schema(description = "정책 내용 게시글")
        String content
) {
    public static PolicyInfoResponse from(Policy policy){
        return new PolicyInfoResponse(
                policy.getId(),
                policy.getPolicyType(),
                policy.getTitle(),
                policy.getContent()
        );
    }
}
