package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

public record AttendanceInfoResponse(
        Long attendanceId,
        Long attendanceRecordId,
        AttendanceType attendanceType,
        AttendanceStatus attendanceStatus
) {
    public static AttendanceInfoResponse from(AttendanceRecord attendanceRecord) {
        return new AttendanceInfoResponse(
                attendanceRecord.getAttendance().getId(),
                attendanceRecord.getId(),
                attendanceRecord.getAttendanceType(),
                attendanceRecord.getAttendanceStatus()
        );
    }
}
