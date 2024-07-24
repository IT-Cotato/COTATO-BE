package org.cotato.csquiz.domain.auth.cache;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailRedisRepository {

    private static final int EXPIRATION_TIME = 15;
    private final RedisTemplate<String, String> redisTemplate;

    public Boolean saveEmail(EmailType signup, final String email){
        String key = signup.getKeyPrefix() + email;
        return redisTemplate.opsForValue().setIfAbsent(
                key,
                signup.getValue(),
                EXPIRATION_TIME,
                TimeUnit.MINUTES
        );
    }
}
