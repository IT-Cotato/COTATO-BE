package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDateTime;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;

public record AttendanceTimeResponse(
        Long sessionId,
        LocalDateTime attendanceDeadLine,
        LocalDateTime lateDeadLine,
        Location location
) {

    public static AttendanceTimeResponse from(Attendance attendance) {
        return new AttendanceTimeResponse(
                attendance.getSessionId(),
                attendance.getAttendanceDeadLine(),
                attendance.getLateDeadLine(),
                attendance.getLocation()
        );
    }
}
