package org.cotato.csquiz.domain.recruitment.repository;

import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentNotificationRequesterRepository extends
        JpaRepository<RecruitmentNotificationRequester, Long> {
    boolean existsByEmailAndSendStatus(String recruitEmail, SendStatus sendStatus);
}
