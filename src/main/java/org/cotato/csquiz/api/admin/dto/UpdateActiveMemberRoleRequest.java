package org.cotato.csquiz.api.admin.dto;

import org.cotato.csquiz.domain.auth.enums.MemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateActiveMemberRoleRequest(
        @NotNull
        Long memberId,
        @NotNull
        MemberRole role
) {
}
