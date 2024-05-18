package cotato.csquiz.controller.dto.member;

import jakarta.validation.constraints.NotNull;

public record MemberRejectRequest(
        @NotNull
        Long memberId
) {
}
