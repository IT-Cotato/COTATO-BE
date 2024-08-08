package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceStatus {
    PRESENT("출석", "출석에 성공하셨습니다."),
    LATE("지각", "기준 시간을 지나 지각 처리 되었습니다."),
    ABSENT("결석", "지각 마감 시간을 지나 결석 처리 되었습니다.")
    ;

    private final String description;
    private final String message;
}
