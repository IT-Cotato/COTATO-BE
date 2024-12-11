package org.cotato.csquiz.domain.education.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QuizCategory {
    NETWORK("네트워크"),
    DB("데이터 베이스"),
    OS("운영 체제"),
    OTHER("기타"),
    ;

    private final String description;
}
