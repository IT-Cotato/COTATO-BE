package org.cotato.csquiz.domain.generation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenerationMemberRole {
    MEMBER("일반부원"),
    LEADER_TEAM("회장단"),
    OPERATION_SUPPORT_TEAM("운영지원팀"),
    EDUCATION_TEAM("교육팀"),
    PLANNING_TEAM("기획팀"),
    MARKETING_TEAM("홍보팀"),
    ;

    private final String description;
}
