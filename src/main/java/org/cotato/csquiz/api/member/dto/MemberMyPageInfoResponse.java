package org.cotato.csquiz.api.member.dto;

import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.entity.Member;

public record MemberMyPageInfoResponse(
        Long memberId,
        String email,
        String name,
        Integer generationNumber,
        MemberPosition memberPosition,
        String phoneNumber
) {
    public static MemberMyPageInfoResponse of(Member member, String originPhoneNumber) {
        return new MemberMyPageInfoResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPassedGenerationNumber(),
                member.getPosition(),
                originPhoneNumber
        );
    }
}
