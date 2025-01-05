package org.cotato.csquiz.domain.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {
    REJECTED("가입 신청 후 거절된 부원"),
    REQUESTED("최초 가입 신청한 대기 상태의 부원"),
    RETIRED("수료 후 활동을 종료한 부원"),
    APPROVED("가입 신청 후 승인된 부원")
    ;
    private final String description;
}
