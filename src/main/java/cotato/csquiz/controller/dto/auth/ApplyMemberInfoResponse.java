package cotato.csquiz.controller.dto.auth;

import cotato.csquiz.domain.entity.Member;

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
