package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.enums.MemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateActiveMemberRoleRequest(
        @NotNull
        Long memberId,
        @NotNull
        MemberRole role
) {
}
