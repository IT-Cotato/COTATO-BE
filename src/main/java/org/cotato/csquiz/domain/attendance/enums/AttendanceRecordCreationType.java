package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceRecordCreationType {

    OFFLINE("대면 출석"),
    ONLINE("비대면 출석"),
    NO_ATTEND("출결 미 입력")
    ;

    private final String description;
}
