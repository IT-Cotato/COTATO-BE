package org.cotato.csquiz.domain.recruitment.entity;

import java.time.LocalDateTime;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentNotificationRequester extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "requester_id")
	private Long id;

	@Column(name = "request_email", nullable = false)
	private String email;

	@Column(name = "request_time", nullable = false)
	private LocalDateTime requestTime;

	@Column(name = "policy_checked", nullable = false)
	private Boolean policyChecked;

	@Column(name = "send_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private SendStatus sendStatus;

	private RecruitmentNotificationRequester(String email, Boolean policyChecked, LocalDateTime requestTime,
		SendStatus sendStatus) {
		this.email = email;
		this.policyChecked = policyChecked;
		this.requestTime = requestTime;
		this.sendStatus = sendStatus;
	}

	public void updateSendStatus(SendStatus sendStatus) {
		this.sendStatus = sendStatus;
	}

	public static RecruitmentNotificationRequester of(String email, Boolean policyChecked) {
		return new RecruitmentNotificationRequester(email, policyChecked, LocalDateTime.now(), SendStatus.NOT_SENT);
	}
}
