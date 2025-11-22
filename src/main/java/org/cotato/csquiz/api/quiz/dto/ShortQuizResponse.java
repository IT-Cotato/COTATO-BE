package org.cotato.csquiz.api.quiz.dto;

import java.util.ArrayList;
import java.util.List;

import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.ShortAnswer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortQuizResponse {

	private Long id;
	private Integer number;
	private String question;
	private String image;
	private List<ShortAnswerResponse> shortAnswers = new ArrayList<>();

	public static ShortQuizResponse of(Quiz quiz, List<ShortAnswer> shortAnswers) {
		return new ShortQuizResponse(
			quiz.getId(),
			quiz.getNumber(),
			quiz.getQuestion(),
			quiz.getImageUrl(),
			shortAnswers.stream().map(ShortAnswerResponse::from).toList()
		);
	}
}
