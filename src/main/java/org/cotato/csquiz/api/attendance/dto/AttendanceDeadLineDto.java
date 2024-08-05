package org.cotato.csquiz.api.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Builder;
import org.cotato.csquiz.domain.attendance.enums.DeadLine;

public record AttendanceDeadLineDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime attendanceDeadLine,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime lateDeadLine
) {

    @Builder
    public AttendanceDeadLineDto {
        if (Objects.isNull(attendanceDeadLine)) {
            attendanceDeadLine = DeadLine.DEFAULT_ATTENDANCE_DEADLINE.getTime();
        }
        if (Objects.isNull(lateDeadLine)) {
            lateDeadLine = DeadLine.DEFAULT_LATE_DEADLINE.getTime();
        }
    }
}
