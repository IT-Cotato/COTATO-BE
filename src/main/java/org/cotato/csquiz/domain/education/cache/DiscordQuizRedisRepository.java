package org.cotato.csquiz.domain.education.cache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiscordQuizRedisRepository {

    private static final String KEY_PREFIX = "$discord_quiz";
    private static final long QUIZ_EXPIRATION = 7 * 24 * 60;
    private static final long EXISTS_VALUE = 1;
    private final RedisTemplate<String, Long> redisTemplate;

    public void save(Long quizId) {
        redisTemplate.opsForValue().set(
                generateKey(quizId),
                EXISTS_VALUE,
                QUIZ_EXPIRATION,
                TimeUnit.MINUTES
        );
    }

    public boolean isUsedInOneWeek(Long quizId){
        return Objects.equals(redisTemplate.opsForValue().get(generateKey(quizId)), EXISTS_VALUE);
    }

    private String generateKey(Long quizId) {
        return KEY_PREFIX + quizId;
    }
}
