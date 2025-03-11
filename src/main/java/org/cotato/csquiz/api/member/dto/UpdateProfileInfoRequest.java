package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.cotato.csquiz.domain.auth.enums.ImageUpdateStatus;

public record UpdateProfileInfoRequest(
        @NotNull
        @Schema(description = "프로필 이미지 변환 여부")
        ImageUpdateStatus imageUpdateStatus,

        @Schema(description = "자기 소개")
        String introduction,

        @Schema(description = "소속 학교")
        String university,

        @Schema(description = "링크 목록")
        List<ProfileLinkRequest> profileLinks
) {
}
