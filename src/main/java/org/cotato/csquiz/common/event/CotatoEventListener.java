package org.cotato.csquiz.common.event;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.service.EmailNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CotatoEventListener {

    private final EmailNotificationService emailNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleCotatoEvent(EmailSendEvent event) {
        switch (event.getType()) {
            case APPROVE_MEMBER -> emailNotificationService.sendSignUpApprovedToEmail(event.getData());
            case REJECT_MEMBER -> emailNotificationService.sendSignupRejectionToEmail(event.getData());
        }
    }
}
