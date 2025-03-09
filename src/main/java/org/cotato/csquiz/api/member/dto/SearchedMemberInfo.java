package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;

public record SearchedMemberInfo(
        @Schema(description = "멤버 Id", requiredMode = RequiredMode.REQUIRED)
        Long memberId,
        @Schema(description = "이름", requiredMode = RequiredMode.REQUIRED)
        String name,
        @Schema(description = "합격 기수", requiredMode = RequiredMode.REQUIRED)
        Integer generationNumber,
        @Schema(description = "포지션", requiredMode = RequiredMode.REQUIRED)
        MemberPosition position
) {
    public static SearchedMemberInfo from(Member member) {
        return new SearchedMemberInfo(member.getId(),
                member.getName(),
                member.getPassedGenerationNumber(),
                member.getPosition());
    }
}
