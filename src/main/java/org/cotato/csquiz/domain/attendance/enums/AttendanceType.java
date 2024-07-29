package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceType {

    OFFLINE("대면 출석"),
    ONLINE("비대면 출석")
    ;

    private final String description;
}
