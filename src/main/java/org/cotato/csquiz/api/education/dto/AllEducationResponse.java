package org.cotato.csquiz.api.education.dto;

import org.cotato.csquiz.domain.education.entity.Education;

public record AllEducationResponse(
	Long educationId,
	String subject,
	Integer educationNumber
) {

	public static AllEducationResponse from(Education education) {
		return new AllEducationResponse(
			education.getId(),
			education.getSubject(),
			education.getNumber()
		);
	}
}
