package org.cotato.csquiz.domain.education.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QuizCategory {
    NETWORK("네트워크"),
    DB("데이터 베이스"),
    OS("OS"),
    OTHER("기타"),
    ;

    private final String description;
}
