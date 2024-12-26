package org.cotato.csquiz.api.quiz.dto;

import jakarta.validation.constraints.NotNull;

public record RandomQuizReplyRequest(
        @NotNull
        Integer input
) {
}
