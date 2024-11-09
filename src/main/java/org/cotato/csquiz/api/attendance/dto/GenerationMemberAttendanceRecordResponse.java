package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.auth.entity.Member;


public record GenerationMemberAttendanceRecordResponse(
        AttendanceMemberInfo memberInfo,
        AttendanceStatistic statistic
) {
    public static GenerationMemberAttendanceRecordResponse of(Member member, AttendanceStatistic attendanceStatistic) {
        return new GenerationMemberAttendanceRecordResponse(
                AttendanceMemberInfo.from(member),
                attendanceStatistic
        );
    }
}
