package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.generation.enums.GenerationMemberRole;

public record UpdateGenerationMemberRoleRequest(
        @NotNull(message = "역할을 입력해주세요")
        GenerationMemberRole role
) {
}
