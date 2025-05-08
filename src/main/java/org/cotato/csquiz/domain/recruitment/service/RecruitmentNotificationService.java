package org.cotato.csquiz.domain.recruitment.service;

import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentNotificationLogDto;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentNotificationLogsResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRequesterRepository;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationEmailLogReader;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationReader;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationRequesterReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentNotificationService {

    private final RecruitmentNotificationEmailLogReader recruitmentNotificationEmailLogReader;
    private final RecruitmentNotificationReader recruitmentNotificationReader;
    private final RecruitmentNotificationRequesterReader recruitmentNotificationRequesterReader;
    private final RecruitmentNotificationRequesterRepository recruitmentNotificationRequesterRepository;

    @Transactional
    public void requestRecruitmentNotification(String recruitEmail, boolean isPolicyChecked) {
        if (!isPolicyChecked) {
            throw new AppException(ErrorCode.SHOULD_AGREE_POLICY);
        }
        if (recruitmentNotificationRequesterReader.existsByEmailAndSendStatus(recruitEmail, SendStatus.NOT_SENT)) {
            throw new AppException(ErrorCode.ALREADY_REQUEST_NOTIFICATION);
        }

        recruitmentNotificationRequesterRepository.save(
                RecruitmentNotificationRequester.of(recruitEmail, isPolicyChecked)
        );
    }

    @Transactional(readOnly = true)
    public RecruitmentNotificationLogsResponse findNotificationLogs() {
        List<RecruitmentNotification> top5Notification = recruitmentNotificationReader.findTopNLatestNotifications(5);

        List<Long> top5NotificationIds = top5Notification.stream()
                .map(RecruitmentNotification::getId)
                .toList();
        HashMap<Long, List<RecruitmentNotificationEmailLog>> logsByNotificationId = recruitmentNotificationEmailLogReader.groupByNotificationIds(
                top5NotificationIds);

        List<RecruitmentNotificationLogDto> dto = top5Notification.stream()
                .map(notification -> RecruitmentNotificationLogDto.of(
                        notification,
                        logsByNotificationId.getOrDefault(notification.getId(), List.of())
                ))
                .toList();
        return RecruitmentNotificationLogsResponse.of(dto);
    }
}
