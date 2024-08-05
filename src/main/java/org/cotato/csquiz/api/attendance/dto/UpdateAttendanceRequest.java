package org.cotato.csquiz.api.attendance.dto;

import static org.cotato.csquiz.domain.attendance.constant.DeadLineUtil.DEFAULT_END_TIME;
import static org.cotato.csquiz.domain.attendance.constant.DeadLineUtil.DEFAULT_START_TIME;

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
            attendanceDeadLine = new AttendanceDeadLineDto(DEFAULT_START_TIME, DEFAULT_END_TIME);
        }
    }
}
