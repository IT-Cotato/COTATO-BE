package org.cotato.csquiz.api.attendance.dto;

import static org.cotato.csquiz.domain.attendance.constant.DeadLineConstants.DEFAULT_END_TIME;
import static org.cotato.csquiz.domain.attendance.constant.DeadLineConstants.DEFAULT_START_TIME;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Builder;

public record AttendanceDeadLineDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime attendanceDeadLine,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime lateDeadLine
) {

    @Builder
    public AttendanceDeadLineDto {
        if (Objects.isNull(attendanceDeadLine)) {
            attendanceDeadLine = DEFAULT_START_TIME;
        }
        if (Objects.isNull(lateDeadLine)) {
            lateDeadLine = DEFAULT_END_TIME;
        }
    }
}
