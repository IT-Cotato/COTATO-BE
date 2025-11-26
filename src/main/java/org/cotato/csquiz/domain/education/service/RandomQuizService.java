package org.cotato.csquiz.domain.education.service;

import org.cotato.csquiz.api.quiz.dto.RandomQuizReplyResponse;
import org.cotato.csquiz.api.quiz.dto.RandomTutorialQuizResponse;
import org.cotato.csquiz.domain.education.entity.RandomQuiz;
import org.cotato.csquiz.domain.education.service.component.RandomQuizReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RandomQuizService {

	private final RandomQuizReader randomQuizReader;

	public RandomTutorialQuizResponse pickRandomQuiz() {
		return RandomTutorialQuizResponse.from(randomQuizReader.getRandomQuiz());
	}

	public RandomQuizReplyResponse replyToRandomQuiz(final Long quizId, final Integer input) {
		RandomQuiz randomQuiz = randomQuizReader.findById(quizId);
		return RandomQuizReplyResponse.from(randomQuiz.isCorrect(input));
	}
}
