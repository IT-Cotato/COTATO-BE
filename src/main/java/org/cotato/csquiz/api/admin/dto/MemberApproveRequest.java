package org.cotato.csquiz.api.admin.dto;

import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import jakarta.validation.constraints.NotNull;

public record MemberApproveRequest(
        @Deprecated(since = "신입 감자 가입 승인 개발 이후")
        @NotNull
        Long memberId,
        @NotNull
        MemberPosition position,
        @NotNull
        Long generationId
) {
}
