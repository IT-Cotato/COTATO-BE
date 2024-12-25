package org.cotato.csquiz.api.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record RandomQuizReplyResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        boolean result
) {
    public static RandomQuizReplyResponse from(Boolean isCorrect) {
        return new RandomQuizReplyResponse(isCorrect);
    }
}
