package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.common.validator.Password;

public record UpdatePasswordRequest(
        @NotNull
        @Password
        String password
) {
}
