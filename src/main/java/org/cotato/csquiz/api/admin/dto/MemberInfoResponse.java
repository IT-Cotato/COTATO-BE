package org.cotato.csquiz.api.admin.dto;

import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.entity.Member;

public record MemberInfoResponse(
        Long memberId,
        String memberName,
        String backFourNumber,
        MemberRole role
) {
    public static MemberInfoResponse from(Member member, String backFourNumber) {
        return new MemberInfoResponse(
                member.getId(),
                member.getName(),
                backFourNumber,
                member.getRole()
        );
    }
}
