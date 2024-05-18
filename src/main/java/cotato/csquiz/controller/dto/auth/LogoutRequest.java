package cotato.csquiz.controller.dto.auth;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull
        String accessToken
) {
}
