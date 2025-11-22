package org.cotato.csquiz.domain.auth.cache;

import java.util.concurrent.TimeUnit;

import org.cotato.csquiz.domain.auth.enums.EmailType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VerificationCodeRedisRepository {

	private static final Integer VERIFICATION_CODE_EXPIRATION_TIME = 10;
	private static final String KEY_PREFIX = "$email$";
	private final RedisTemplate<String, String> redisTemplate;

	public String getByEmail(EmailType type, String email) {
		String queryKey = type.getKeyPrefix() + KEY_PREFIX + email;
		return redisTemplate.opsForValue().get(queryKey);
	}

	public void saveCodeWithEmail(EmailType type, String email, String verificationCode) {
		String saveKey = type.getKeyPrefix() + KEY_PREFIX + email;
		redisTemplate.opsForValue().set(
			saveKey,
			verificationCode,
			VERIFICATION_CODE_EXPIRATION_TIME,
			TimeUnit.MINUTES
		);
	}
}
