package org.cotato.csquiz.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.cotato.csquiz.domain.auth.entity.Member;

public record JoinResponse(
        @Schema(description = "가입된 회원 PK")
        Long memberId
) {
    public static JoinResponse from(Member member) {
        return new JoinResponse(
                member.getId()
        );
    }
}
