package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateSessionDescriptionRequest(
        @NotNull
        Long sessionId,
        @NotNull
        String description
) {
}
