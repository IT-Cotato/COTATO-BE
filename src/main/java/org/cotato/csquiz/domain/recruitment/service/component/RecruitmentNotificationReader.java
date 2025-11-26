package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.List;

import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentNotificationReader {

	private final RecruitmentNotificationRepository recruitmentNotificationRepository;

	public List<RecruitmentNotification> findTop5LatestNotifications() {
		return recruitmentNotificationRepository.findTop5ByOrderBySendTimeDesc();
	}
}
