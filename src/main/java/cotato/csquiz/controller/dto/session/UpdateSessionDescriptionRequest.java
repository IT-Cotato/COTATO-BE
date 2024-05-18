package cotato.csquiz.controller.dto.session;

import jakarta.validation.constraints.NotNull;

public record UpdateSessionDescriptionRequest(
        @NotNull
        Long sessionId,
        @NotNull
        String description
) {
}
