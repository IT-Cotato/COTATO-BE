package org.cotato.csquiz.domain.recruitment.repository;

import java.util.List;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentNotificationRepository extends JpaRepository<RecruitmentNotification, Long> {

    List<RecruitmentNotification> findAllByOrderBySendTimeDesc(PageRequest pageRequest);
}
