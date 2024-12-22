package org.cotato.csquiz.api.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public record ReplyResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        boolean result
) {

    public static ReplyResponse from(Boolean isCorrect) {
        return new ReplyResponse(isCorrect);
    }
}
