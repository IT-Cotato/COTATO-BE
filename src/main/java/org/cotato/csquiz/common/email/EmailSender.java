package org.cotato.csquiz.common.email;

import static org.cotato.csquiz.domain.auth.constant.EmailConstants.SENDER_EMAIL;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.SENDER_PERSONAL;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    public void sendEmail(String recipient, String messageBody, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.addRecipients(RecipientType.TO, recipient);
            message.setSubject(subject);
            message.setText(messageBody, "utf-8", "html");
            message.setFrom(getInternetAddress());
            mailSender.send(message);
            log.info("이메일 전송 완료");
        } catch (MessagingException e) {
            throw new AppException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    private InternetAddress getInternetAddress() {
        try {
            return new InternetAddress(SENDER_EMAIL, SENDER_PERSONAL);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
