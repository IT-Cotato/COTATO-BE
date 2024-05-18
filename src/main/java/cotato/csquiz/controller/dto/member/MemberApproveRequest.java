package cotato.csquiz.controller.dto.member;

import cotato.csquiz.domain.enums.MemberPosition;
import jakarta.validation.constraints.NotNull;

public record MemberApproveRequest(
        @NotNull
        Long memberId,
        @NotNull
        MemberPosition position,
        @NotNull
        Long generationId
) {
}
