package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;

public record AttendanceTimeResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long sessionId,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        LocalDateTime attendanceDeadLine,
        @Schema(requiredMode = RequiredMode.REQUIRED)
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
