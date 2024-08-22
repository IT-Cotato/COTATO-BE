package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalTime;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;

public record AttendanceTimeResponse(
        Long sessionId,
        LocalTime attendanceDeadLine,
        LocalTime lateDeadLine,
        Location location
) {

    public static AttendanceTimeResponse from(Attendance attendance) {
        return new AttendanceTimeResponse(
                attendance.getSessionId(),
                attendance.getAttendanceDeadLine().toLocalTime(),
                attendance.getLateDeadLine().toLocalTime(),
                attendance.getLocation()
        );
    }
}
