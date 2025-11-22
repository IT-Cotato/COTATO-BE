package org.cotato.csquiz.common.util;

import static org.cotato.csquiz.domain.auth.constant.EmailConstants.*;

import org.cotato.csquiz.domain.auth.entity.Member;

public class EmailUtil {

	public static String createSignupApprovedMessageBody(Member recipientMember) {
		StringBuilder sb = new StringBuilder();
		return String.valueOf(sb.append(SIGNUP_MESSAGE_PREFIX)
			.append(getMemberName(recipientMember.getName()))
			.append(SIGNUP_SUCCESS_MESSAGE)
			.append(String.format(MEMBER_GENERATION_PREFIX, recipientMember.getPassedGenerationNumber()))
			.append(String.format(MEMBER_POSITION_PREFIX, recipientMember.getPosition().name()))
			.append(COTATO_HYPERLINK));
	}

	public static String createOldMemberConversionEmailBody(Member recipientMember) {
		StringBuilder sb = new StringBuilder();
		return String.valueOf(sb.append(SIGNUP_MESSAGE_PREFIX)
			.append(getMemberName(recipientMember.getName()))
			.append(CONVERSION_TO_OM_MESSAGE)
			.append(COTATO_HYPERLINK));
	}

	public static String createSignupRejectionMessageBody(Member recipientMember) {
		StringBuilder sb = new StringBuilder();
		return String.valueOf(sb.append(SIGNUP_MESSAGE_PREFIX)
			.append(getMemberName(recipientMember.getName()))
			.append(SIGNUP_FAIL_MESSAGE)
			.append(COTATO_HYPERLINK));
	}

	public static String getVerificationMessageBody(String verificationCode) {
		StringBuilder sb = new StringBuilder();
		return String.valueOf(sb.append(MESSAGE_PREFIX)
			.append(verificationCode)
			.append(MESSAGE_SUFFIX));
	}

	private static String getMemberName(String memberName) {
		return String.format(MEMBER_NAME_SUFFIX, memberName);
	}
}
