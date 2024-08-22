package org.cotato.csquiz.api.education.dto;

import org.cotato.csquiz.domain.education.entity.Winner;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.entity.Member;

public record WinnerInfoResponse(
        Long memberId,
        String name,
        Long educationId,
        String backFourNumber,
        MemberPosition position
) {
    public static WinnerInfoResponse of(Winner winner, Member member, String backFourNumber) {
        return new WinnerInfoResponse(
                member.getId(),
                member.getName(),
                winner.getEducation().getId(),
                backFourNumber,
                member.getPosition()
        );
    }
}
