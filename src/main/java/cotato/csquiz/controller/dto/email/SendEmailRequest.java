package cotato.csquiz.controller.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SendEmailRequest(
        @Email
        @NotNull
        String email
) {
}
