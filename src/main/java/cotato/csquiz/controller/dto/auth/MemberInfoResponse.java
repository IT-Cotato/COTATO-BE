package cotato.csquiz.controller.dto.auth;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberRole;

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
