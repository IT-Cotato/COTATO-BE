package cotato.csquiz.controller.dto.member;

import jakarta.validation.constraints.NotNull;

public record CheckPasswordRequest(
        @NotNull
        String password
) {
}
