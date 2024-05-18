package cotato.csquiz.controller.dto.session;

import jakarta.validation.constraints.NotNull;

public record UpdateSessionNumberRequest(
        @NotNull
        Long sessionId,
        @NotNull
        Integer sessionNum
) {
}
