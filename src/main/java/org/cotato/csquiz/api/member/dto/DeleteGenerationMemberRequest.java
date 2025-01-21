package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteGenerationMemberRequest(
        @NotNull(message = "기수별 멤버 pk를 입력해 주세요")
        Long generationMemberId
) {
}
