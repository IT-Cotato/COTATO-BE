package org.cotato.csquiz.api.socket.dto;

public record EducationResultResponse(
	String command,
	Long educationId
) {
	public static EducationResultResponse of(String command, Long educationId) {
		return new EducationResultResponse(command, educationId);
	}

}
