package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;

public record CreateGenerationMemberRequest(
        @NotNull(message = "멤버 아이디를 입력해주세요")
        Long memberId,
        @NotNull(message = "기수를 입력해주세요")
        Long generationId
) {
}
