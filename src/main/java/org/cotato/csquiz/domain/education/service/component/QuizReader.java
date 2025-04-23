package org.cotato.csquiz.domain.education.service.component;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.education.cache.DiscordQuizRedisRepository;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class QuizReader {

    private static final int MAX_DISCORD_QUIZ_LENGTH = 80;

    private final EducationReader educationReader;
    private final QuizRepository quizRepository;
    private final DiscordQuizRedisRepository discordQuizRedisRepository;

    @Transactional(readOnly = true)
    public Quiz getRandomDiscordQuiz() {
        List<Education> finishedEducations = educationReader.getAllByStatus(EducationStatus.FINISHED);

        List<Quiz> multipleQuizzes = quizRepository.findMultipleQuizzesByEducationInAndQuestionLengthLE(finishedEducations, MAX_DISCORD_QUIZ_LENGTH).stream()
                .filter(quiz -> !discordQuizRedisRepository.isUsedInOneWeek(quiz.getId()))
                .toList();

        if (multipleQuizzes.isEmpty()) {
            throw new EntityNotFoundException("디스코드에 전송할 랜덤 퀴즈가 없습니다.");
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Quiz quiz = multipleQuizzes.get(random.nextInt(multipleQuizzes.size()));
        discordQuizRedisRepository.save(quiz.getId());

        return quiz;
    }
}
