package org.cotato.csquiz.domain.recruitment.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentInformationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentInformationInitializer {

    private final RecruitmentInformationRepository recruitmentInformationRepository;

    @PostConstruct
    @Transactional
    public void initRecruitmentInformation() {
        if (recruitmentInformationRepository.count() == 0) {
            log.info("create init recruitmentInformation");
            RecruitmentInformation initInformation = RecruitmentInformation.builder()
                    .isOpened(false)
                    .build();
            recruitmentInformationRepository.save(initInformation);
        }
    }
}
