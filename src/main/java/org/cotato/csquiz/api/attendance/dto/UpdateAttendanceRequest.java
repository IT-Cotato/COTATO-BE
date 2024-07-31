package org.cotato.csquiz.api.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import org.cotato.csquiz.domain.attendance.embedded.Location;

public record UpdateAttendanceRequest(
        Long attendanceId,
        Location location,

        @NotNull
        AttendanceDeadLine attendanceDeadLine
) {
    public record AttendanceDeadLine(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
            LocalTime startTime,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
            LocalTime endTime
    ){
    }
}
