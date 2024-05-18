package org.cotato.csquiz.domain.auth.service;

import static org.cotato.csquiz.domain.auth.constant.EmailConstants.MESSAGE_PREFIX;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.MESSAGE_SUFFIX;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.SENDER_EMAIL;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.SENDER_PERSONAL;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.auth.utils.EmailFormValidator;
import org.cotato.csquiz.domain.auth.cache.VerificationCodeRedisRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerificationService {

    private static final int CODE_LENGTH = 6;
    private static final int CODE_BOUNDARY = 10;

    private final JavaMailSender mailSender;
    private final VerificationCodeRedisRepository verificationCodeRedisRepository;
    private final EmailFormValidator emailFormValidator;

    @Transactional
    public void sendVerificationCodeToEmail(String recipient, String subject) {
        emailFormValidator.validateEmailForm(recipient);
        String verificationCode = getVerificationCode();
        log.info("인증 번호 생성 완료");
        verificationCodeRedisRepository.saveCodeWithEmail(recipient, verificationCode);
        sendEmailWithVerificationCode(recipient, verificationCode, subject);
    }

    private String getVerificationCode() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            builder.append(random.nextInt(CODE_BOUNDARY));
        }
        return String.valueOf(builder);
    }

    private void sendEmailWithVerificationCode(String recipient, String verificationCode, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(RecipientType.TO, recipient);
            message.setSubject(subject);
            message.setText(getVerificationText(verificationCode), "utf-8", "html");
            message.setFrom(getInternetAddress());
            mailSender.send(message);
            log.info("이메일 전송 완료");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getVerificationText(String verificationCode) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb.append(MESSAGE_PREFIX)
                .append(verificationCode)
                .append(MESSAGE_SUFFIX));
    }

    private InternetAddress getInternetAddress() {
        try {
            return new InternetAddress(SENDER_EMAIL, SENDER_PERSONAL);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void verifyCode(String email, String code) {
        String savedVerificationCode = verificationCodeRedisRepository.getByEmail(email);
        validateEmailCodeMatching(savedVerificationCode, code);
        log.info("[이메일 인증 완료]: 성공한 이메일 == {}", email);
    }

    private void validateEmailCodeMatching(String savedVerificationCode, String code) {
        if (!savedVerificationCode.equals(code)) {
            throw new AppException(ErrorCode.CODE_NOT_MATCH);
        }
    }
}
