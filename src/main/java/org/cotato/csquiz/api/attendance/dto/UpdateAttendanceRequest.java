package org.cotato.csquiz.api.attendance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.attendance.embedded.Location;

public record UpdateAttendanceRequest(
        @NotNull
        Long attendanceId,
        Location location,

        @Valid
        @NotNull
        AttendanceDeadLineDto attendanceDeadLine
) {
}
