package org.cotato.csquiz.api.attendance.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Objects;
import org.cotato.csquiz.domain.attendance.embedded.Location;

public record UpdateAttendanceRequest(
        @NotNull
        Long attendanceId,
        Location location,
        AttendanceDeadLineDto attendanceDeadLine
) {
    public static final LocalTime DEFAULT_START_TIME = LocalTime.of(19, 0);
    public static final LocalTime DEFAULT_END_TIME = LocalTime.of(19, 20);

    public UpdateAttendanceRequest {
        if (Objects.isNull(attendanceDeadLine)) {
            attendanceDeadLine = new AttendanceDeadLineDto(DEFAULT_START_TIME, DEFAULT_END_TIME);
        }
    }
}
