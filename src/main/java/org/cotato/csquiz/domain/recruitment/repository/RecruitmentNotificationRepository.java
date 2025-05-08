package org.cotato.csquiz.domain.recruitment.repository;

import java.util.List;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentNotificationRepository extends JpaRepository<RecruitmentNotification, Long> {

    @Query("""
            SELECT n
              FROM RecruitmentNotification n
              JOIN FETCH n.sender
             ORDER BY n.sendTime DESC
            """)
    List<RecruitmentNotification> findRecentSendTimeFetchJoinSender(PageRequest pageRequest);
}
