package org.cotato.csquiz.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.domain.auth.event.EmailSendEvent;
import org.cotato.csquiz.domain.auth.service.EmailNotificationService;
import org.cotato.csquiz.domain.generation.event.SessionImageEvent;
import org.cotato.csquiz.domain.generation.service.SessionImageService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CotatoEventListener {

    private final EmailNotificationService emailNotificationService;

    private final SessionImageService sessionImageService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleEmailSentEvent(EmailSendEvent event) {
        log.info("Handling email send event: {}", event.getType());
        switch (event.getType()) {
            case APPROVE_MEMBER -> emailNotificationService.sendSignUpApprovedToEmail(event.getData().member());
            case REJECT_MEMBER -> emailNotificationService.sendSignupRejectionToEmail(event.getData().member());
            default -> throw new AppException(ErrorCode.EVENT_TYPE_EXCEPTION);
        }
    }

    @EventListener
    public void handleSessionImageUpdateEvent(SessionImageEvent event) throws ImageException {
        log.info("Handling session image update event: {}", event.getType());
        switch (event.getType()) {
            case SESSION_IMAGE_UPDATE -> sessionImageService.addSessionImages(event.getData().getImages(), event.getData().getSession());
            default -> throw new AppException(ErrorCode.EVENT_TYPE_EXCEPTION);
        }
    }
}
