package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;

public record AttendanceMemberInfo(
        Long memberId,
        String name,
        MemberPosition position
) {
    public static AttendanceMemberInfo from(Member member) {
        return new AttendanceMemberInfo(
                member.getId(),
                member.getName(),
                member.getPosition()
        );
    }
}
