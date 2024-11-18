package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceResult {
    ONLINE("대면 출석", "대면으로 출석했습니다.", true),
    OFFLINE("비대면 출석", "비대면으로 출석했습니다.", true),
    LATE("지각", "기준 시간을 지나 지각 처리 되었습니다.", false),
    ABSENT("결석", "지각 마감 시간을 지나 결석 처리 되었습니다.", false)
    ;

    private final String description;
    private final String message;
    private final boolean isPresented;
}
