package org.cotato.csquiz.api.auth.dto;

import org.cotato.csquiz.common.config.jwt.Token;

public record ReissueResponse(
	String accessToken
) {
	public static ReissueResponse from(final Token token) {
		return new ReissueResponse(token.getAccessToken());
	}
}
