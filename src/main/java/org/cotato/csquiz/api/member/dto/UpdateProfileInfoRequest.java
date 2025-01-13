package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record UpdateProfileInfoRequest(
        @Schema(description = "자기 소개")
        String introduction,

        @Schema(description = "소속 학교")
        String university,

        @Schema(description = "링크 목록")
        List<ProfileLinkRequest> profileLinks
) {
}
