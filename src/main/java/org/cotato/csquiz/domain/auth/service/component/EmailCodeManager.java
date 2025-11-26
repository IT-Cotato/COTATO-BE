package org.cotato.csquiz.domain.auth.service.component;

import java.util.concurrent.ThreadLocalRandom;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.cache.EmailRedisRepository;
import org.cotato.csquiz.domain.auth.cache.VerificationCodeRedisRepository;
import org.cotato.csquiz.domain.auth.enums.EmailType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailCodeManager {

	private static final int CODE_LENGTH = 6;
	private static final int CODE_BOUNDARY = 10;
	private final VerificationCodeRedisRepository verificationCodeRedisRepository;
	private final EmailRedisRepository emailRedisRepository;

	public String getRandomCode(final EmailType type, final String recipient) {
		String verificationCode = getRandomCode();
		emailRedisRepository.saveEmail(type, recipient);
		verificationCodeRedisRepository.saveCodeWithEmail(type, recipient, verificationCode);

		return verificationCode;
	}

	private String getRandomCode() {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < CODE_LENGTH; i++) {
			builder.append(random.nextInt(CODE_BOUNDARY));
		}
		return String.valueOf(builder);
	}

	public void verifyCode(EmailType type, String email, String code) {
		String savedVerificationCode = verificationCodeRedisRepository.getByEmail(type, email);
		if (savedVerificationCode != null) {
			validateEmailCodeMatching(savedVerificationCode, code);
			log.info("[이메일 인증 완료]: 성공한 이메일 == {}", email);
			return;
		}

		if (emailRedisRepository.isEmailPresent(type, email)) {
			throw new AppException(ErrorCode.CODE_EXPIRED);
		} else {
			throw new AppException(ErrorCode.REQUEST_AGAIN);
		}
	}

	private void validateEmailCodeMatching(String savedVerificationCode, String code) {
		if (!savedVerificationCode.equals(code)) {
			throw new AppException(ErrorCode.CODE_NOT_MATCH);
		}
	}
}
