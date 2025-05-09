package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationEmailLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentNotificationEmailLogReader {

    private final RecruitmentNotificationEmailLogRepository recruitmentNotificationEmailLogRepository;

    public Map<Long, List<RecruitmentNotificationEmailLog>> groupByNotificationIds(List<Long> ids) {
        return recruitmentNotificationEmailLogRepository.findAllByNotificationIdIn(ids).stream()
                .collect(Collectors.groupingBy(
                        log -> log.getNotification().getId(),
                        Collectors.toList()
                ));
    }
}
