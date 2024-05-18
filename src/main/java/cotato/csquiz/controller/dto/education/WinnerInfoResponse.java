package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.domain.enums.MemberPosition;

public record WinnerInfoResponse(
        Long memberId,
        String memberName,
        Long educationNumber,
        String backFourNumber,
        MemberPosition memberPosition
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
