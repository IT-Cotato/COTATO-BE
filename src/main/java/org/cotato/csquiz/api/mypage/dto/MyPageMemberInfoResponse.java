package org.cotato.csquiz.api.mypage.dto;

import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.entity.Member;

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
