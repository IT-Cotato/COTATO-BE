package org.cotato.csquiz.api.quiz.dto;

import jakarta.validation.constraints.NotNull;

public record AddAdditionalAnswerRequest(
        @NotNull
        Long quizId,
        @NotNull
        String answer
) {
}
