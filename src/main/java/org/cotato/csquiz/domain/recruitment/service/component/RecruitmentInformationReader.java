package org.cotato.csquiz.domain.recruitment.service.component;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentInformationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentInformationReader {

    private final RecruitmentInformationRepository recruitmentInformationRepository;

    public RecruitmentInformation findRecruitmentInformation() {
        return recruitmentInformationRepository.findAll()
                .get(0);
    }
}
