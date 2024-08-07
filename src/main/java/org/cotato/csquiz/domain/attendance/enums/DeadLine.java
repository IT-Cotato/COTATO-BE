package org.cotato.csquiz.domain.attendance.enums;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeadLine {

    ATTENDANCE_START_TIME(LocalTime.of(18, 50, 0), "고정 출석 시작 시간"),
    DEFAULT_ATTENDANCE_DEADLINE(LocalTime.of(19, 5, 59), "기본 출석 마감 시간"),
    DEFAULT_LATE_DEADLINE(LocalTime.of(19,20,59),"기본 지각 마감 시간"),
    ATTENDANCE_END_TIME(LocalTime.of(20, 0,0), "고정 세션 종료 시간")
    ;

    private final LocalTime time;
    private final String description;
}
