package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceOpenStatus {
    CLOSED("출결 진행 중이 아닙니다."),
    OPEN("현재 출석 진행 중"),
    LATE("현재 출결 입력 시 지각"),
    ABSENT("현재 출결 입력 시 결석")
    ;

    private final String description;
}
