package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordResult;

public record UpdateAttendanceRecordRequest(
        Long memberId,
        AttendanceRecordResult attendanceResult
) {
}