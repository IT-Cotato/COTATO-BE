package org.cotato.csquiz.domain.recruitment.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.generation.embedded.Period;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.cotato.csquiz.domain.schedule.RecruitmentScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RecruitmentInformationService {

    private final RecruitmentInformationReader recruitmentInformationReader;
    private final RecruitmentScheduler recruitmentScheduler;

    @Transactional(readOnly = true)
    public RecruitmentInfoResponse findRecruitmentInfo() {
        RecruitmentInformation info = recruitmentInformationReader.findRecruitmentInformation();
        if (info.isOpened()) {
            return RecruitmentInfoResponse.opened(info);
        }
        return RecruitmentInfoResponse.closed();
    }

    @Transactional
    public void changeRecruitmentInfo(final Boolean isOpened, final LocalDate startDate, final LocalDate endDate,
                                      String recruitmentUrl) {
        RecruitmentInformation info = recruitmentInformationReader.findRecruitmentInformation();

        if (isOpened) {
            validateOpenParameters(startDate, endDate, recruitmentUrl);
        }
        info.changeOpened(isOpened);
        info.changePeriod(Period.of(startDate, endDate));
        info.changeRecruitmentUrl(recruitmentUrl);

        //TODO https://youthing.atlassian.net/jira/software/projects/COT/boards/2?selectedIssue=COT-274&sprints=9
        registerScheduleSync(isOpened, endDate);
    }

    //트랜잭션이 커밋된 후에 스케쥴 등록
    private void registerScheduleSync(Boolean isOpened, LocalDate endDate) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                recruitmentScheduler.cancelCloseRecruitmentScheduler();
                if (isOpened) {
                    recruitmentScheduler.registerCloseRecruitmentScheduler(endDate);
                }
            }
        });
    }

    private void validateOpenParameters(LocalDate startDate, LocalDate endDate, String recruitmentUrl) {
        if (startDate == null || endDate == null || recruitmentUrl == null || recruitmentUrl.isBlank()) {
            throw new AppException(ErrorCode.INVALID_RECRUITMENT_INFO);
        }
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.INVALID_DATE);
        }
    }
}
