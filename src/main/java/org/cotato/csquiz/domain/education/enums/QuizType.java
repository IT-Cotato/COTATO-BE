package org.cotato.csquiz.domain.education.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuizType {
    MULTIPLE_QUIZ("객관식 문제"),
    SHORT_QUIZ("주관식 문제");

    private final String description;
}
