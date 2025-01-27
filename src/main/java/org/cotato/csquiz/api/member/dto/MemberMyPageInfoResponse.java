package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.auth.entity.Member;

public record MemberMyPageInfoResponse(
        @Schema(description = "이메일", requiredMode = RequiredMode.REQUIRED)
        String email,
        @Schema(description = "전화번호", requiredMode = RequiredMode.REQUIRED)
        String phoneNumber
) {
    public static MemberMyPageInfoResponse of(Member member, String originPhoneNumber) {
        return new MemberMyPageInfoResponse(
                member.getEmail(),
                originPhoneNumber
        );
    }
}
