package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberPosition;
import cotato.csquiz.domain.enums.MemberRole;

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
