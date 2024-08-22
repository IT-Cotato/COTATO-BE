package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;


public record AttendanceRecordResponse(
        AttendanceMemberInfo memberInfo,
        AttendanceStatistic statistic
) {
    public static AttendanceRecordResponse of(Member member, AttendanceStatistic attendanceStatistic) {
        return new AttendanceRecordResponse(
                AttendanceMemberInfo.from(member),
                attendanceStatistic
        );
    }

    public record AttendanceMemberInfo(
            Long memberId,
            String name,
            MemberPosition position
    ){
        static AttendanceMemberInfo from(Member member) {
            return new AttendanceMemberInfo(
                    member.getId(),
                    member.getName(),
                    member.getPosition()
            );
        }
    }
}
