package org.cotato.csquiz.api.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.auth.entity.ProfileLink;
import org.cotato.csquiz.domain.auth.enums.UrlType;

public record ProfileLinkResponse(
        @Schema(description = "링크 pk", requiredMode = RequiredMode.REQUIRED)
        Long linkId,
        @Schema(description = "링크 타입", requiredMode = RequiredMode.REQUIRED)
        UrlType urlType,
        @Schema(description = "링크 url", requiredMode = RequiredMode.REQUIRED)
        String url
) {
    public static ProfileLinkResponse from(ProfileLink profileLink) {
        return new ProfileLinkResponse(
                profileLink.getId(),
                profileLink.getUrlType(),
                profileLink.getUrl()
        );
    }
}
