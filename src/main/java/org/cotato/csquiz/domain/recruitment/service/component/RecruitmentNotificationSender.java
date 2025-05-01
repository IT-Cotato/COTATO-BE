package org.cotato.csquiz.domain.recruitment.service.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.email.AwsMailSender;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationEmailLogRepository;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRequesterRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentNotificationSender {

    private final AwsMailSender awsMailSender;
    private final RecruitmentNotificationRequesterRepository recruitmentNotificationRequesterRepository;
    private final RecruitmentNotificationEmailLogRepository recruitmentNotificationEmailLogRepository;

    @Async("emailSendThreadPoolExecutor")
    public void sendNotificationAsync(final RecruitmentNotificationRequester requester,
                                      final RecruitmentNotification recruitmentNotification,
                                      final String htmlBody, final String subject
    ) {
        boolean success = true;
        try {
            awsMailSender.sendRawMessageBody(
                    requester.getEmail(),
                    htmlBody,
                    subject
            );
            requester.updateSendStatus(SendStatus.SUCCESS);
        } catch (Exception e) {
            success = false;
            requester.updateSendStatus(SendStatus.FAIL);
            log.info("메일 전송 실패 email: {} exception: {}", requester.getEmail(), e.getMessage());
        }

        recruitmentNotificationRequesterRepository.save(requester);
        recruitmentNotificationEmailLogRepository.save(RecruitmentNotificationEmailLog.of(
                requester,
                recruitmentNotification,
                success
        ));
    }
}
