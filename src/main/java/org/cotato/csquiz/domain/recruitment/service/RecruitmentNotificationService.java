package org.cotato.csquiz.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRequesterRepository;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationRequesterReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitmentNotificationService {

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
}
