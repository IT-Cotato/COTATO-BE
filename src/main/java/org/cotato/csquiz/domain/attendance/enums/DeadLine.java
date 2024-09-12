package org.cotato.csquiz.domain.attendance.enums;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeadLine {

    DEFAULT_ATTENDANCE_DEADLINE(LocalTime.of(19, 5, 0), "기본 출석 마감 시간"),
    DEFAULT_LATE_DEADLINE(LocalTime.of(19,20,0),"기본 지각 마감 시간"),
    ;

    private final LocalTime time;
    private final String description;
}
