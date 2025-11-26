package org.cotato.csquiz.domain.recruitment.email;

import org.cotato.csquiz.common.config.property.CotatoProperties;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecruitmentEmailFactory {

	private final CotatoProperties properties;

	public EmailContent getRecruitmentEmailContent(int generation) {

		String subject = "[IT 연합동아리 코테이토] " + generation + "기 모집이 시작되었습니다.";

		String htmlBody = """
			<html>
			<body style="margin:0; padding:0; font-family:-apple-system, BlinkMacSystemFont,
			'Segoe UI', Roboto, Helvetica, Arial, sans-serif; font-size:16px; color:#000;">
			<table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background:#f4f4f4; padding:20px 0;">
			<tr>
			<td align="center">
			<!-- 컨테이너 테이블 -->
			<table width="640" cellpadding="0" cellspacing="0" border="0" style="background:#fff; padding:34px 0;">
			<!-- 헤더 -->
			<tr>
			<td align="center" style="padding-bottom:28px;">
			<h1 style="margin:0; font-size:24px; font-weight:600;">
			코테이토 <span style="color:#ffa000;">%d기</span> 모집이 시작되었습니다!
			</h1>
			</td>
			</tr>
			<!-- 본문 텍스트 -->
			<tr>
			<td align="center" style="padding-bottom:28px;">
			<p style="margin:4px 0;">
			무엇이라도 해내야겠다는 마음가짐, 발전하고자 하는 열정이면 충분합니다.
			</p>
			<p style="margin:4px 0;">
			<a href="%s" style="color:#ffa000; text-decoration:underline;">코테이토 홈페이지</a>에 접속하여 지원해주세요!
			</p>
			</td>
			</tr>
			<!-- 버튼 -->
			<tr>
			<td align="center" style="padding-bottom:28px;">
			<a href="%s" style="display:inline-block; padding:12px 36px; border:1px solid #ffa000; background-color:#ffc700; border-radius:16px; text-decoration:none; font-weight:500; color:#000;">
			홈페이지 바로가기
			</a>
			</td>
			</tr>
			<!-- 푸터 -->
			<tr>
			<td align="center" style="background:#f4f4f4; padding:20px;">
			<p style="margin:0; font-size:12px; color:#c6c4c1;">
			© Cotato. 2024 All rights reserved.
			</p>
			</td>
			</tr>
			</table>
			</td>
			</tr>
			</table>
			</body>
			</html>
			""".formatted(
			generation,
			properties.getBaseUrl(),
			properties.getBaseUrl()
		);

		return new EmailContent(subject, htmlBody);
	}

	public EmailContent getRequestSuccessEmailContent() {
		String subject = "[코테이토] 모집 알림 신청이 정상적으로 완료되었습니다.";
		String htmlBody = "모집 알림 신청이 정상적으로 완료되었습니다.";

		return new EmailContent(subject, htmlBody);
	}
}
