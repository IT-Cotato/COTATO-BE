package org.cotato.csquiz.api.auth.dto;

public record FindPasswordResponse(
	String accessToken
) {
	public static FindPasswordResponse from(String accessToken) {
		return new FindPasswordResponse(accessToken);
	}
}
