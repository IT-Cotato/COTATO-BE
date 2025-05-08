package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentNotificationReader {

    private final RecruitmentNotificationRepository recruitmentNotificationRepository;

    public List<RecruitmentNotification> findTopNLatestNotifications(int limit) {
        return recruitmentNotificationRepository.findRecentSendTimeFetchJoinSender(PageRequest.of(0, limit));
    }
}
