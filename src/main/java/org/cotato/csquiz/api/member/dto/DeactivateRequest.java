package org.cotato.csquiz.api.member.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.cotato.csquiz.api.policy.dto.CheckPolicyRequest;

public record DeactivateRequest(
        @NotNull
        @Email
        String email,
        @NotNull
        String password,

        @NotNull @Valid
        List<CheckPolicyRequest> checkedPolicies
) {
}
