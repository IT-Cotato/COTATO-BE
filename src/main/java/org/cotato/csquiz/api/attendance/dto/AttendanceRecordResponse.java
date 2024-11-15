package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordResult;
import org.cotato.csquiz.domain.auth.entity.Member;

public record AttendanceRecordResponse(
        AttendanceMemberInfo memberInfo,
        AttendanceRecordResult result
) {
    public static AttendanceRecordResponse of(Member member, AttendanceRecordResult result) {
        return new AttendanceRecordResponse(
                AttendanceMemberInfo.from(member),
                result
        );
    }
}
