package org.cotato.csquiz.domain.education.service.component;

import jakarta.persistence.EntityNotFoundException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.education.entity.RandomQuiz;
import org.cotato.csquiz.domain.education.repository.RandomQuizRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RandomQuizReader {

    private final RandomQuizRepository randomQuizRepository;

    public RandomQuiz getRandomQuiz() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        int totalCount = (int) randomQuizRepository.count();
        if (totalCount == 0) {
            throw new EntityNotFoundException("랜덤 퀴즈가 존재하지 않습니다");
        }

        int randomPage = random.nextInt(totalCount);
        Page<RandomQuiz> quizPage = randomQuizRepository.findAll(PageRequest.of(randomPage, 1));
        return quizPage.getContent().get(0);
    }
}
