package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.auth.entity.Member;


public record AttendanceRecordStatisticResponse(
        AttendanceMemberInfo memberInfo,
        AttendanceStatistic statistic
) {
    public static AttendanceRecordStatisticResponse of(Member member, AttendanceStatistic attendanceStatistic) {
        return new AttendanceRecordStatisticResponse(
                AttendanceMemberInfo.from(member),
                attendanceStatistic
        );
    }
}
