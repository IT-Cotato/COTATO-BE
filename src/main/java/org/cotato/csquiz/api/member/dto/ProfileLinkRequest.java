package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.auth.enums.UrlType;

public record ProfileLinkRequest(
        @NotNull(message = "링크 타입을 지정해주세요")
        UrlType urlType,

        @NotBlank(message = "링크를 입력해주세요")
        String link
) {
}
