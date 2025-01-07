package org.cotato.csquiz.api.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.auth.entity.Member;

public record ApplyMemberInfoResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long id,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        String name,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        String backFourNumber
) {

    public static ApplyMemberInfoResponse from(Member member, String backFourNumber) {
        return new ApplyMemberInfoResponse(
                member.getId(),
                member.getName(),
                backFourNumber
        );
    }
}
