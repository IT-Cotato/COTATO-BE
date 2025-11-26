package org.cotato.csquiz.api.education.dto;

import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.enums.EducationStatus;

public record FindEducationStatusResponse(
	EducationStatus status
) {
	public static FindEducationStatusResponse from(Education education) {
		return new FindEducationStatusResponse(education.getStatus());
	}
}
