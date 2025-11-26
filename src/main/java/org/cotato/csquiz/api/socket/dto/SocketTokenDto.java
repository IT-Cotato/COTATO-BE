package org.cotato.csquiz.api.socket.dto;

public record SocketTokenDto(
	String socketToken
) {
	public static SocketTokenDto from(String socketToken) {
		return new SocketTokenDto(
			socketToken
		);
	}
}
