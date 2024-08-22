package org.cotato.csquiz.api.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Builder;
import org.cotato.csquiz.domain.attendance.enums.DeadLine;

public record AttendanceDeadLineDto(
        @Schema(example = "19:05:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime attendanceDeadLine,
        @Schema(example = "19:20:00")
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
