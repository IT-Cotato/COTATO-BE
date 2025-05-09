package org.cotato.csquiz.domain.recruitment.repository;

import java.util.List;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RecruitmentNotificationRequesterRepository extends
        JpaRepository<RecruitmentNotificationRequester, Long> {
    boolean existsByEmailAndSendStatus(String recruitEmail, SendStatus sendStatus);

    List<RecruitmentNotificationRequester> findAllBySendStatusIn(List<SendStatus> status);

    @Transactional
    @Modifying
    @Query("update RecruitmentNotificationRequester r set r.sendStatus = :status where r.id in :ids")
    void updateSendStatusByIds(@Param("status") SendStatus sendStatus, @Param("ids") List<Long> ids);
}
