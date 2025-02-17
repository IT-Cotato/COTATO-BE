package org.cotato.csquiz.api.admin.dto;

import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import jakarta.validation.constraints.NotNull;

public record MemberApproveRequest(
        @NotNull
        MemberPosition position,
        @NotNull
        Long generationId
) {
}
