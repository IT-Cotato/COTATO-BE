package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.auth.entity.Member;

public record KingMemberInfo(
        Long memberId,
        String name,
        String backFourNumber
) {
    public static KingMemberInfo from(Member member, String backFourNumber) {
        return new KingMemberInfo(
                member.getId(),
                member.getName(),
                backFourNumber
        );
    }
}
