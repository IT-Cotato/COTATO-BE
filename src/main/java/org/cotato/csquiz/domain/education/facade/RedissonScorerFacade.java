package org.cotato.csquiz.domain.education.facade;

import java.util.concurrent.TimeUnit;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Record;
import org.cotato.csquiz.domain.education.service.ScorerService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonScorerFacade {

	private static final String KEY_PREFIX = "$Scorer_lock_";
	private final ScorerService scorerService;
	private final RedissonClient redissonClient;

	public void checkAndThenUpdateScorer(Record memberReply) {
		RLock lock = redissonClient.getLock(generateKey(memberReply.getQuiz()));

		try {
			boolean available = lock.tryLock(30, 1, TimeUnit.SECONDS);

			if (!available) {
				log.error("[락 획득 실패 (Record : {})]", memberReply.getId());
				throw new AppException(ErrorCode.SCORER_LOCK_ERROR);
			}
			scorerService.checkAndThenUpdateScorer(memberReply);

		} catch (InterruptedException e) {
			throw new AppException(ErrorCode.SCORER_LOCK_ERROR);
		} finally {
			if (lock.isLocked() && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	private String generateKey(final Quiz quiz) {
		return KEY_PREFIX + quiz.getId();
	}
}
