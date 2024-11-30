package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateGenerationMemberRequest(
        @NotNull(message = "요청 리스트를 입력해주세요")
        List<CreateGenerationMember> members
) {
}
