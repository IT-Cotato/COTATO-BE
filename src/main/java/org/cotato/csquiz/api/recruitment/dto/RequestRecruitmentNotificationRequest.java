package org.cotato.csquiz.api.recruitment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RequestRecruitmentNotificationRequest(
        @NotNull
        Boolean policyCheck,
        @Email
        @NotNull
        String email
) {
}
