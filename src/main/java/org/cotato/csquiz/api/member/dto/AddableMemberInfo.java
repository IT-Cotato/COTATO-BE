package org.cotato.csquiz.api.member.dto;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;

public record AddableMemberInfo(
        Long memberId,
        String name,
        Integer generationNumber,
        MemberPosition memberPosition
) {
    public static AddableMemberInfo from(Member member) {
        return new AddableMemberInfo(member.getId(),
                member.getName(),
                member.getPassedGenerationNumber(),
                member.getPosition());
    }
}
