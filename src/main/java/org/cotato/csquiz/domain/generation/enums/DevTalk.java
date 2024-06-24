package org.cotato.csquiz.domain.generation.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DevTalk {
    DEVTALK_ON("데브톡 존재"),
    DEVTALK_OFF("데브톡 없음")
    ;
    private final String description;
}
