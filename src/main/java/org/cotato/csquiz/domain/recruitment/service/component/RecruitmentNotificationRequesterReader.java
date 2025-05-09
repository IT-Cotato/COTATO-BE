package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRequesterRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentNotificationRequesterReader {

    private final RecruitmentNotificationRequesterRepository recruitmentNotificationRequesterRepository;

    public boolean existsByEmailAndSendStatus(String email, SendStatus sendStatus) {
        return recruitmentNotificationRequesterRepository.existsByEmailAndSendStatus(email, sendStatus);
    }

    public List<RecruitmentNotificationRequester> findAllNotSentOrFailEmails() {
        return recruitmentNotificationRequesterRepository.findAllBySendStatusIn(
                List.of(SendStatus.NOT_SENT, SendStatus.FAIL));
    }
}
