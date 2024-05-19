package org.cotato.csquiz.api.admin.dto;

import org.cotato.csquiz.domain.auth.entity.Member;

public record ApplyMemberInfoResponse(
        Long id,
        String name,
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
