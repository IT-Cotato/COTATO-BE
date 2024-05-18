package cotato.csquiz.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum QuizType {
    MULTIPLE_QUIZ("객관식 문제"),
    SHORT_QUIZ("주관식 문제");

    private final String description;
}
