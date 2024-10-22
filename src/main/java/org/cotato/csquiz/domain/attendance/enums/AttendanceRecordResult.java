package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceRecordResult {
    OFFLINE("대면 출석"),
    ONLINE("비대면 출석"),
    LATE("지각"),
    ABSENT("결석"),
    ;

    private final String description;
}
