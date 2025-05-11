package org.cotato.csquiz.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    APPROVE_MEMBER("부원 가입 승인"),
    REJECT_MEMBER("부원 가입 거절");

    private final String description;
}
