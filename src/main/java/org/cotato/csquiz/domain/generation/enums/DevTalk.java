package org.cotato.csquiz.domain.generation.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DevTalk {
    ON("데브톡 존재"),
    OFF("데브톡 없음")
    ;
    private final String description;
}
