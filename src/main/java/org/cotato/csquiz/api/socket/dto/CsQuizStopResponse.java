package org.cotato.csquiz.api.socket.dto;

public record CsQuizStopResponse(
	String command,
	Long educationId
) {
	public static CsQuizStopResponse from(String command, Long educationId) {
		return new CsQuizStopResponse(
			command,
			educationId
		);
	}
}
