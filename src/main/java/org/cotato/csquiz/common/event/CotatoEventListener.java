package org.cotato.csquiz.common.event;

import lombok.RequiredArgsConstructor;
 import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.event.EmailSendEvent;
import org.cotato.csquiz.domain.auth.service.EmailNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CotatoEventListener {

    private final EmailNotificationService emailNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleEmailSentEvent(EmailSendEvent event) {
        switch (event.getType()) {
            case APPROVE_MEMBER -> emailNotificationService.sendSignUpApprovedToEmail(event.getData().member());
            case REJECT_MEMBER -> emailNotificationService.sendSignupRejectionToEmail(event.getData().member());
            default -> throw new AppException(ErrorCode.EVENT_TYPE_EXCEPTION);
        }
    }
}
