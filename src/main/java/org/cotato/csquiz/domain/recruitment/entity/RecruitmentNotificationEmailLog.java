package org.cotato.csquiz.domain.recruitment.entity;

import org.cotato.csquiz.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentNotificationEmailLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private RecruitmentNotificationRequester receiver;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notification_id", nullable = false)
	private RecruitmentNotification notification;

	@Column(name = "send_success")
	private Boolean sendSuccess;

	private RecruitmentNotificationEmailLog(RecruitmentNotificationRequester receiver,
		RecruitmentNotification notification, boolean sendSuccess) {
		this.receiver = receiver;
		this.notification = notification;
		this.sendSuccess = sendSuccess;
	}

	public static RecruitmentNotificationEmailLog of(RecruitmentNotificationRequester receiver,
		RecruitmentNotification notification, boolean sendSuccess) {
		return new RecruitmentNotificationEmailLog(receiver, notification, sendSuccess);
	}
}
