package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;

public record CheckPasswordRequest(
        @NotNull
        String password
) {
}
