package org.cotato.csquiz.domain.recruitment.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.generation.embedded.Period;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitmentInformationService {

    private final RecruitmentInformationReader recruitmentInformationReader;

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
    }

    private void validateOpenParameters(LocalDate startDate, LocalDate endDate, String recruitmentUrl) {
        if (startDate == null || endDate == null || recruitmentUrl == null || recruitmentUrl.isBlank()) {
            throw new AppException(ErrorCode.INVALID_RECRUITMENT_INFO);
        }
    }
}
