package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;

public record MemberInfoResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long memberId,
        String name,
        String backFourNumber,
        MemberRole role,
        MemberStatus status,
        MemberPosition position
) {
    public static MemberInfoResponse from(Member member, String backFourNumber) {
        return new MemberInfoResponse(
                member.getId(),
                member.getName(),
                backFourNumber,
                member.getRole(),
                member.getStatus(),
                member.getPosition()
        );
    }
}
