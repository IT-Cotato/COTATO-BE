package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberPosition;
import cotato.csquiz.domain.enums.MemberRole;

public record MemberInfo(
        Long memberId,
        String name,
        String email,
        String backFourNumber,
        MemberRole memberRole,
        MemberPosition position
) {
    public static MemberInfo of(Member findMember, String backFourNumber) {
        return new MemberInfo(
                findMember.getId(),
                findMember.getName(),
                findMember.getEmail(),
                backFourNumber,
                findMember.getRole(),
                findMember.getPosition()
        );
    }
}
