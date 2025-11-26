package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.ShortAnswer;

public record ShortAnswerResponse(
	String answer
) {
	public static ShortAnswerResponse from(ShortAnswer shortAnswer) {
		return new ShortAnswerResponse(
			shortAnswer.getContent()
		);
	}
}
