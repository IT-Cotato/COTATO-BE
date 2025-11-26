package org.cotato.csquiz.domain.auth.service;

import static org.cotato.csquiz.common.util.EmailUtil.*;
import static org.cotato.csquiz.domain.auth.constant.EmailConstants.*;

import org.cotato.csquiz.common.email.EmailSender;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

	private final EmailSender emailSender;

	public void sendSignUpApprovedToEmail(Member recipientMember) {
		String successMessage = createSignupApprovedMessageBody(recipientMember);

		emailSender.sendEmail(recipientMember.getEmail(), successMessage, SIGNUP_SUCCESS_SUBJECT);
		log.info("가입 승인 완료 이메일 전송 완료");
	}

	public void sendSignupRejectionToEmail(Member recipientMember) {
		String rejectMessage = createSignupRejectionMessageBody(recipientMember);

		emailSender.sendEmail(recipientMember.getEmail(), rejectMessage, SIGNUP_REJECT_SUBJECT);
		log.info("가입 승인 거절 이메일 전송 완료");
	}

	public void sendOldMemberConversionToEmail(Member recipientMember) {
		String conversionMessageBody = createOldMemberConversionEmailBody(recipientMember);

		emailSender.sendEmail(recipientMember.getEmail(), conversionMessageBody, CONVERSION_TO_OM_SUBJECT);
		log.info("OM 전환 이메일 전송 완료");
	}
}
