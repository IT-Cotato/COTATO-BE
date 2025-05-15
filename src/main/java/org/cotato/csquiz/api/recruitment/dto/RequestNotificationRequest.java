package org.cotato.csquiz.api.recruitment.dto;

import jakarta.validation.constraints.NotNull;

public record RequestNotificationRequest(
        @NotNull
        Integer generationNumber
) {
}
