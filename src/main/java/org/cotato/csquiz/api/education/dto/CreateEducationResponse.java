package org.cotato.csquiz.api.education.dto;

import org.cotato.csquiz.domain.education.entity.Education;

public record CreateEducationResponse(
        Long educationId
) {
    public static CreateEducationResponse from(Education education) {
        return new CreateEducationResponse(education.getId());
    }
}
