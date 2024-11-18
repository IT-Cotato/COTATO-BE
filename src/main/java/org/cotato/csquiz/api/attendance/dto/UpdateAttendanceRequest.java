package org.cotato.csquiz.api.attendance.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.attendance.embedded.Location;

public record UpdateAttendanceRequest(
        @NotNull
        Long attendanceId,
        Location location,
        AttendanceDeadLineDto attendTime
) {
    public static UpdateAttendanceRequest of(Long attendanceId, Location location, AttendanceDeadLineDto attendTime) {
        return new UpdateAttendanceRequest(attendanceId, location, attendTime);
    }
}
