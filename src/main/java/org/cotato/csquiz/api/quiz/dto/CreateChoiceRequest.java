package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;

import lombok.Data;

@Data
public class CreateChoiceRequest {

	private String content;
	private Integer number;
	private ChoiceCorrect isAnswer;
}
