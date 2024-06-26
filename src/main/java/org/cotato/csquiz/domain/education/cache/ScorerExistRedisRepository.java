package org.cotato.csquiz.domain.education.cache;

import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScorerExistRedisRepository {

    private static final String KEY_PREFIX = "$Scorer for ";
    private static final Long NONE_VALUE = Long.MAX_VALUE;
    private static final Integer SCORER_EXPIRATION = 60 * 24;
    private final RedisTemplate<String, Long> redisTemplate;

    public void saveAllScorerNone(List<Quiz> quizzes) {
        for (Quiz quiz : quizzes) {
            String quizKey = KEY_PREFIX + quiz.getId();
            redisTemplate.opsForValue().set(
                    quizKey,
                    NONE_VALUE,
                    SCORER_EXPIRATION,
                    TimeUnit.MINUTES
            );
        }
    }

    public void saveScorer(Quiz quiz, Long ticketNumber) {
        String quizKey = KEY_PREFIX + quiz.getId();
        redisTemplate.opsForValue().set(
                quizKey,
                ticketNumber,
                SCORER_EXPIRATION,
                TimeUnit.MINUTES
        );
    }

    public boolean saveScorerIfIsFastest(Quiz quiz, Long ticketNumber) {
        if (getScorerTicketNumber(quiz) > ticketNumber) {
            saveScorer(quiz, ticketNumber);
            return true;
        } else {
            return false;
        }
    }

    public Long getScorerTicketNumber(Quiz quiz) {
        String quizKey = KEY_PREFIX + quiz.getId();
        if (redisTemplate.opsForValue().get(quizKey) == null) {
            return NONE_VALUE;
        }
        return redisTemplate.opsForValue().get(quizKey);
    }
}
