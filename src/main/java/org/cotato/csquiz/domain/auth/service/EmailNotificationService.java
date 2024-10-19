package org.cotato.csquiz.domain.auth.service;

import static org.cotato.csquiz.common.util.EmailUtil.createOldMemberConversionEmailBody;
import static org.cotato.csquiz.common.util.EmailUtil.createSignupApprovedMessageBody;
import static org.cotato.csquiz.common.util.EmailUtil.createSignupRejectionMessageBody;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.CONVERSION_TO_OM_SUBJECT;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.SIGNUP_REJECT_SUBJECT;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.SIGNUP_SUCCESS_SUBJECT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.email.EmailService;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.utils.EmailFormValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final EmailService emailService;
    private final EmailFormValidator emailFormValidator;

    public void sendSignUpApprovedToEmail(Member recipientMember) {
        emailFormValidator.validateEmailForm(recipientMember.getEmail());

        String successMessage = createSignupApprovedMessageBody(recipientMember);

        emailService.sendEmail(recipientMember.getEmail(), successMessage, SIGNUP_SUCCESS_SUBJECT);
        log.info("가입 승인 완료 이메일 전송 완료");
    }

    public void sendSignupRejectionToEmail(Member recipientMember) {
        emailFormValidator.validateEmailForm(recipientMember.getEmail());

        String rejectMessage = createSignupRejectionMessageBody(recipientMember);

        emailService.sendEmail(recipientMember.getEmail(), rejectMessage, SIGNUP_REJECT_SUBJECT);
        log.info("가입 승인 거절 이메일 전송 완료");
    }

    public void sendOldMemberConversionToEmail(Member recipientMember) {
        emailFormValidator.validateEmailForm(recipientMember.getEmail());

        String conversionMessageBody = createOldMemberConversionEmailBody(recipientMember);

        emailService.sendEmail(recipientMember.getEmail(), conversionMessageBody, CONVERSION_TO_OM_SUBJECT);
        log.info("OM 전환 이메일 전송 완료");
    }
}
