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
        AttendanceDeadLineDto attendTime
) {

    public UpdateAttendanceRequest {
        if (Objects.isNull(attendTime)) {
            attendTime = new AttendanceDeadLineDto(DEFAULT_ATTENDANCE_DEADLINE.getTime(),
                    DEFAULT_LATE_DEADLINE.getTime());
        }
    }

    public static UpdateAttendanceRequest of(Long attendanceId, Location location, AttendanceDeadLineDto attendTime) {
        return new UpdateAttendanceRequest(attendanceId, location, attendTime);
    }
}
