package cotato.csquiz.controller.dto.mypage;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberPosition;
import cotato.csquiz.domain.enums.MemberRole;

public record MyPageMemberInfoResponse(
        Long memberId,
        String memberName,
        String phoneNumber,
        Integer generationNumber,
        MemberRole memberRole,
        MemberPosition memberPosition
) {
    public static MyPageMemberInfoResponse of(Member member, String originPhoneNumber) {
        return new MyPageMemberInfoResponse(
                member.getId(),
                member.getName(),
                originPhoneNumber,
                member.getPassedGenerationNumber(),
                member.getRole(),
                member.getPosition()
        );
    }
}
