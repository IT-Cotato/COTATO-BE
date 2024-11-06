package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.auth.entity.Member;


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
}
