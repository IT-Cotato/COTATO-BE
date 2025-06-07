package org.cotato.csquiz.api.auth.dto;

import org.cotato.csquiz.common.config.jwt.Token;

public record ReissueResponse(
        String accessToken,
        String refreshToken
) {
    public static ReissueResponse from(Token token) {
        return new ReissueResponse(token.getAccessToken(), token.getRefreshToken());
    }
}
