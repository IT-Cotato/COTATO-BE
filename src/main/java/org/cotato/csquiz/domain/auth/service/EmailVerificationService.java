package org.cotato.csquiz.domain.auth.service;

import static org.cotato.csquiz.common.util.EmailUtil.getVerificationMessageBody;

import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.email.EmailService;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.auth.cache.EmailRedisRepository;
import org.cotato.csquiz.domain.auth.enums.EmailType;
import org.cotato.csquiz.domain.auth.utils.EmailFormValidator;
import org.cotato.csquiz.domain.auth.cache.VerificationCodeRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerificationService {

    private static final int CODE_LENGTH = 6;
    private static final int CODE_BOUNDARY = 10;

    private final EmailService emailService;
    private final VerificationCodeRedisRepository verificationCodeRedisRepository;
    private final EmailFormValidator emailFormValidator;
    private final EmailRedisRepository emailRedisRepository;

    public void sendVerificationCodeToEmail(EmailType type, String recipient, String subject) {
        emailFormValidator.validateEmailForm(recipient);

        String verificationCode = getVerificationCode();
        log.info("인증 번호 생성 완료");

        emailRedisRepository.saveEmail(type, recipient);
        verificationCodeRedisRepository.saveCodeWithEmail(type, recipient, verificationCode);

        String verificationMessage = getVerificationMessageBody(verificationCode);

        emailService.sendEmail(recipient, verificationMessage, subject);
    }

    private String getVerificationCode() {
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
