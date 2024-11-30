package org.cotato.csquiz.domain.generation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionType {
    NO_ATTEND("출석을 진행하지 않는 세션", false),
    ONLINE("비대면으로만 진행하는 세션", true),
    OFFLINE("대면으로만 진행하는 세션", true),
    ALL("대면, 비대면 혼용", true)
    ;

    private final String description;
    private final boolean createAttendance;
}
