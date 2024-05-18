package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberPosition;

public record MemberMyPageInfoResponse(
        Long memberId,
        String email,
        String name,
        Integer generationNumber,
        MemberPosition memberPosition,
        String phoneNumber
) {
    public static MemberMyPageInfoResponse of(Member member, String originPhoneNumber) {
        return new MemberMyPageInfoResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPassedGenerationNumber(),
                member.getPosition(),
                originPhoneNumber
        );
    }
}
