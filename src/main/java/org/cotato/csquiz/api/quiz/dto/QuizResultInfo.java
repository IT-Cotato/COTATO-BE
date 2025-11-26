package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.education.entity.Quiz;

public record QuizResultInfo(
	Long quizId,
	Integer quizNumber,
	Long scorerId, // Todo : 해당 컬럼명에 대한 고민
	String scorerName,
	String backFourNumber
) {
	public static QuizResultInfo of(Quiz quiz, Member member, String backFourNumber) {
		return new QuizResultInfo(
			quiz.getId(),
			quiz.getNumber(),
			member.getId(),
			member.getName(),
			backFourNumber
		);
	}

	public static QuizResultInfo noScorer(Quiz quiz) {
		return new QuizResultInfo(
			quiz.getId(),
			quiz.getNumber(),
			null,
			null,
			null
		);
	}
}
