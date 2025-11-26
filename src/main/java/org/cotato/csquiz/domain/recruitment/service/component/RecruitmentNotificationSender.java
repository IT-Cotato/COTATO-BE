package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.concurrent.CompletableFuture;

import org.cotato.csquiz.common.email.SesEmailSender;
import org.cotato.csquiz.domain.recruitment.email.EmailContent;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.service.component.dto.NotificationResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentNotificationSender {

	private final SesEmailSender sesEmailSender;

	public CompletableFuture<NotificationResult> sendNotificationAsync(final RecruitmentNotificationRequester requester,
		final EmailContent emailContent) {
		return sendEmailAsync(requester.getEmail(), emailContent)
			.thenApply(v -> NotificationResult.of(requester.getId(), true))
			.exceptionally(e -> {
				log.info("메일 전송 실패 email: {} exception: {}", requester.getEmail(), e.getMessage());
				return NotificationResult.of(requester.getId(), false);
			});
	}

	@Async("emailSendThreadPoolExecutor")
	public CompletableFuture<Void> sendEmailAsync(final String email,
		final EmailContent emailContent) {
		return CompletableFuture.runAsync(() ->
			sesEmailSender.sendRawMessageBody(
				email,
				emailContent.htmlBody(),
				emailContent.subject()
			));
	}
}
