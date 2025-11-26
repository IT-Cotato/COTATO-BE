package org.cotato.csquiz.domain.education.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TicketCountRedisRepository {

	private static final String KEY_PREFIX = "$ticket for ";
	private final RedisTemplate<String, Object> redisTemplate;

	public Long increment(Long quizId) {
		String key = KEY_PREFIX + quizId;
		return redisTemplate.opsForValue()
			.increment(key);
	}
}
