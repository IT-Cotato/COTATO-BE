package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.auth.entity.Member;

public record SingleAttendanceRecordResponse(
        AttendanceMemberInfo memberInfo,
        AttendanceResult result
) {
    public static SingleAttendanceRecordResponse of(Member member, AttendanceResult result) {
        return new SingleAttendanceRecordResponse(
                AttendanceMemberInfo.from(member),
                result
        );
    }
}
