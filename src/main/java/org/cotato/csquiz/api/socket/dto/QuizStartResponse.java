package org.cotato.csquiz.api.socket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizStartResponse {

	private Long quizId;
	private String command;
}
