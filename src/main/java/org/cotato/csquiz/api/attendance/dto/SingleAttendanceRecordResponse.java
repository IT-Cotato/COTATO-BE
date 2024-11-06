package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordResult;
import org.cotato.csquiz.domain.auth.entity.Member;

public record SingleAttendanceRecordResponse(
        AttendanceMemberInfo memberInfo,
        AttendanceRecordResult result
) {
    public static SingleAttendanceRecordResponse of(Member member, AttendanceRecordResult result) {
        return new SingleAttendanceRecordResponse(
                AttendanceMemberInfo.from(member),
                result
        );
    }
}
