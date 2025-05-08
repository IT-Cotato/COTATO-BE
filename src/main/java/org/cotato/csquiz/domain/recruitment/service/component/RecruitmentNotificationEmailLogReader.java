package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.HashMap;
import java.util.List;
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

    public HashMap<Long, List<RecruitmentNotificationEmailLog>> groupByNotificationIds(List<Long> ids) {
        List<RecruitmentNotificationEmailLog> logs = recruitmentNotificationEmailLogRepository.findAllByNotificationIdIn(
                ids);

        return logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getNotification().getId(),
                        HashMap::new,
                        Collectors.toList()
                ));
    }
}
