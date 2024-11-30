package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.auth.enums.MemberRole;

public record UpdateGenerationMemberRoleRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "멤버별 활동 정보를 입력해주세요")
        Long generationMemberId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "역할을 입력해주세요")
        MemberRole role
) {
}
