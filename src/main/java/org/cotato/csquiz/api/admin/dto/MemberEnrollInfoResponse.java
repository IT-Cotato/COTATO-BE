package org.cotato.csquiz.api.admin.dto;

import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.entity.Member;

public record MemberEnrollInfoResponse(
        Long memberId,
        String memberName,
        MemberPosition position,
        Integer generationNumber,
        MemberRole role
) {


    public static MemberEnrollInfoResponse of(Member member) {
        return new MemberEnrollInfoResponse(
                member.getId(),
                member.getName(),
                member.getPosition(),
                member.getPassedGenerationNumber(),
                member.getRole()
        );
    }
}
