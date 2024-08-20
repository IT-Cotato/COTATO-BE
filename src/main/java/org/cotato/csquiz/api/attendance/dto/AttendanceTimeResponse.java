package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record AttendanceTimeResponse(
        Long sessionId,
        LocalTime attendanceDeadLine,
        LocalTime lateDeadLine
) {
    public static AttendanceTimeResponse of(Long sessionId, LocalDateTime attendanceDeadLine, LocalDateTime lateDeadLine) {
        return new AttendanceTimeResponse(
                sessionId,
                attendanceDeadLine.toLocalTime(),
                lateDeadLine.toLocalTime()
        );
    }
}
