package org.cotato.csquiz.domain.auth.cache;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.enums.EmailType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailRedisRepository {

    private static final int EXPIRATION_TIME = 15;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveRequestStatus(String status, EmailType type, final String email) {
        String key = type.getKeyPrefix() + email;
        redisTemplate.opsForValue().set(
                key,
                status,
                EXPIRATION_TIME,
                TimeUnit.MINUTES
        );
    }

    public Boolean isEmailPresent(EmailType type, final String email) {
        String key = type.getKeyPrefix() + email;
        return redisTemplate.hasKey(key);
    }

    public String getValue(EmailType type, final String email) {
        String key = type.getKeyPrefix() + email;
        return redisTemplate.opsForValue().get(key);
    }
}
