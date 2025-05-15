package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.email.SesEmailSender;
import org.cotato.csquiz.domain.recruitment.email.EmailContent;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.service.component.dto.NotificationResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentNotificationSender {

    private final SesEmailSender sesEmailSender;

    @Async("emailSendThreadPoolExecutor")
    public CompletableFuture<NotificationResult> sendNotificationAsync(final RecruitmentNotificationRequester requester,
                                                                       final EmailContent emailContent) {
        return sendEmailAsync(requester, emailContent)
                .thenApply(v -> NotificationResult.of(requester.getId(), true))
                .exceptionally(e -> {
                    log.info("메일 전송 실패 email: {} exception: {}", requester.getEmail(), e.getMessage());
                    return NotificationResult.of(requester.getId(), false);
                });
    }

    private CompletableFuture<Void> sendEmailAsync(final RecruitmentNotificationRequester requester,
                                                   final EmailContent emailContent) {
        return CompletableFuture.runAsync(() ->
                sesEmailSender.sendRawMessageBody(
                        requester.getEmail(),
                        emailContent.htmlBody(),
                        emailContent.subject()
                ));
    }
}
