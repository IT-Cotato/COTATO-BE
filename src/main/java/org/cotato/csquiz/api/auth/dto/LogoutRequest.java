package org.cotato.csquiz.api.auth.dto;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull
        String accessToken
) {
}
