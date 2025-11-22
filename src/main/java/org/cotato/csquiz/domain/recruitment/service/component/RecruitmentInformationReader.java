package org.cotato.csquiz.domain.recruitment.service.component;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
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
		List<RecruitmentInformation> infos = recruitmentInformationRepository.findAll();
		if (infos.size() != 1) {
			throw new AppException(ErrorCode.RECRUITMENT_INFO_COUNT_INVALID);
		}
		return infos.get(0);
	}
}
