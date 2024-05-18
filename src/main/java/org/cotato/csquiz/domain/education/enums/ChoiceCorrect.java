package org.cotato.csquiz.domain.education.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ChoiceCorrect {
    ANSWER("퀴즈 정답"),
    NO_ANSWER("퀴즈 오답"),
    SECRET("정답 여부를 알려주지 않음");

    private final String description;
}
