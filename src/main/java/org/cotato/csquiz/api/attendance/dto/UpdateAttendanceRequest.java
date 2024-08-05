package org.cotato.csquiz.api.attendance.dto;

import static org.cotato.csquiz.domain.attendance.enums.DeadLine.DEFAULT_ATTENDANCE_DEADLINE;
import static org.cotato.csquiz.domain.attendance.enums.DeadLine.DEFAULT_LATE_DEADLINE;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import org.cotato.csquiz.domain.attendance.embedded.Location;

public record UpdateAttendanceRequest(
        @NotNull
        Long attendanceId,
        Location location,
        AttendanceDeadLineDto attendanceDeadLine
) {

    public UpdateAttendanceRequest {
        if (Objects.isNull(attendanceDeadLine)) {
            attendanceDeadLine = new AttendanceDeadLineDto(DEFAULT_ATTENDANCE_DEADLINE.getTime(),
                    DEFAULT_LATE_DEADLINE.getTime());
        }
    }
}
