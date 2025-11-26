package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.QuizStatus;

public record CsAdminQuizResponse(
	Long quizId,
	String question,
	Integer quizNumber,
	QuizStatus status,
	QuizStatus start
) {
	public static CsAdminQuizResponse from(Quiz quiz) {
		return new CsAdminQuizResponse(
			quiz.getId(),
			quiz.getQuestion(),
			quiz.getNumber(),
			quiz.getStatus(),
			quiz.getStart()
		);
	}
}
