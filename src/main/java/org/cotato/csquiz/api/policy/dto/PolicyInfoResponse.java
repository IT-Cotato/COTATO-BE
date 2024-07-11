package org.cotato.csquiz.api.policy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.cotato.csquiz.domain.auth.entity.Policy;

public record PolicyInfoResponse(
        @Schema(description = "정책 PK")
        Long policyId,
        @Schema(description = "정책 타이틀")
        String title,
        @Schema(description = "정책 내용 게시글")
        String content
) {
    public static PolicyInfoResponse from(Policy policy){
        return new PolicyInfoResponse(
                policy.getId(),
                policy.getTitle(),
                policy.getContent()
        );
    }
}
