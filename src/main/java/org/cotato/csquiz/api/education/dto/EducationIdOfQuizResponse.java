package org.cotato.csquiz.api.education.dto;

import org.cotato.csquiz.domain.education.entity.Quiz;

public record EducationIdOfQuizResponse(
	Long educationId
) {
	public static EducationIdOfQuizResponse from(Quiz quiz) {
		return new EducationIdOfQuizResponse(
			quiz.getEducation().getId()
		);
	}
}
