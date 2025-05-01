package org.cotato.csquiz.domain.recruitment.repository;

import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentNotificationEmailLogRepository extends
        JpaRepository<RecruitmentNotificationEmailLog, Long> {
}
