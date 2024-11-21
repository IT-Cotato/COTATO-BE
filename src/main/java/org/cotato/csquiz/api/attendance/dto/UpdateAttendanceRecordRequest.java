package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;

public record UpdateAttendanceRecordRequest(
        Long memberId,
        AttendanceResult attendanceResult
) {
}