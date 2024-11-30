package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.auth.enums.MemberRole;

public record UpdateGenerationMemberRoleRequest(
        @NotNull(message = "멤버별 활동 정보를 입력해주세요")
        Long generationMemberId,
        @NotNull(message = "역할을 입력해주세요")
        MemberRole role
) {
}
