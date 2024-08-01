package org.cotato.csquiz.api.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Builder;

public record AttendanceDeadLineDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime startTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime endTime
) {
    public static final LocalTime DEFAULT_START_TIME = LocalTime.of(19, 0);
    public static final LocalTime DEFAULT_END_TIME = LocalTime.of(19, 20);

    @Builder
    public AttendanceDeadLineDto {
        if (Objects.isNull(startTime)) {
            startTime = DEFAULT_START_TIME;
        }
        if (Objects.isNull(endTime)) {
            endTime = DEFAULT_END_TIME;
        }
    }
}
