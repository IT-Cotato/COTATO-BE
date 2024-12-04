package org.cotato.csquiz.api.member.dto;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;

public record GenerationMemberInfo(
        Long generationMemberId,
        String name,
        MemberPosition position,
        Integer generationNumber,
        MemberRole role
) {
    public static GenerationMemberInfo from(GenerationMember generationMember) {
        Member member = generationMember.getMember();
        return new GenerationMemberInfo(
                generationMember.getId(),
                member.getName(),
                member.getPosition(),
                member.getPassedGenerationNumber(),
                generationMember.getRole()
        );
    }
}
