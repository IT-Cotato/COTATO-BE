package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSessionNumberRequest(
        @NotNull
        Long sessionId,
        @NotNull
        Integer sessionNum
) {
}
