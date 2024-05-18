package org.cotato.csquiz.api.socket.dto;

import jakarta.validation.constraints.NotNull;

public record QuizOpenRequest(
        @NotNull
        Long educationId
) {
}
