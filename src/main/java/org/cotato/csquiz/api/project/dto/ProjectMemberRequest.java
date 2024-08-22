package org.cotato.csquiz.api.project.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;

public record ProjectMemberRequest(
        @NotNull
        String name,
        @NotNull
        MemberPosition position
) {
}
