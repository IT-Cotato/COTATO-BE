package org.cotato.csquiz.domain.education.service;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.quiz.dto.RandomTutorialQuizResponse;
import org.cotato.csquiz.domain.education.entity.RandomQuiz;
import org.cotato.csquiz.domain.education.repository.RandomQuizRepository;
import org.cotato.csquiz.domain.education.service.component.RandomQuizReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RandomQuizService {

    private final RandomQuizReader randomQuizReader;

    public RandomTutorialQuizResponse pickRandomQuiz() {
        return RandomTutorialQuizResponse.from(randomQuizReader.getRandomQuiz());
    }
}
