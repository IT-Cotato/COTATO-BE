package cotato.csquiz.controller.dto.member;

import jakarta.validation.constraints.NotNull;

public record UpdateOldMemberRoleRequest(
        @NotNull
        Long memberId
) {
}
