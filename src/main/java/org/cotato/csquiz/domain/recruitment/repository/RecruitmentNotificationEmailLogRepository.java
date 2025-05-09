package org.cotato.csquiz.domain.recruitment.repository;

import java.util.List;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentNotificationEmailLogRepository extends
        JpaRepository<RecruitmentNotificationEmailLog, Long> {
    List<RecruitmentNotificationEmailLog> findAllByNotificationIdIn(List<Long> ids);
}
