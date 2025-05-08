package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.email.SesEmailSender;
import org.cotato.csquiz.domain.recruitment.email.EmailContent;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
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
        boolean success = true;
        try {
            sesEmailSender.sendRawMessageBody(
                    requester.getEmail(),
                    emailContent.htmlBody(),
                    emailContent.subject()
            );
        } catch (Exception e) {
            success = false;
            requester.updateSendStatus(SendStatus.FAIL);
            log.info("메일 전송 실패 email: {} exception: {}", requester.getEmail(), e.getMessage());
        }

        return CompletableFuture.completedFuture(NotificationResult.of(requester.getId(), success));
    }
}
