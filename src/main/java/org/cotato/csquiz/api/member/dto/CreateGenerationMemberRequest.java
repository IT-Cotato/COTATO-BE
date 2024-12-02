package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateGenerationMemberRequest(
        @NotNull(message = "기수를 입력해 주세요")
        Long generationId,
        @NotNull(message = "멤버 pk를 입력해 주세요")
        List<Long> memberIds
) {
}
