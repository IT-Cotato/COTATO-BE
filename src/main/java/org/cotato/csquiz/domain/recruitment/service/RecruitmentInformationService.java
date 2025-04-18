package org.cotato.csquiz.domain.recruitment.service;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
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
}
