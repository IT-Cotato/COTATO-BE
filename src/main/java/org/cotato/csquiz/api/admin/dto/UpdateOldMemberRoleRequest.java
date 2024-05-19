package org.cotato.csquiz.api.admin.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateOldMemberRoleRequest(
        @NotNull
        Long memberId
) {
}
