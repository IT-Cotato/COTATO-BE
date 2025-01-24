package org.cotato.csquiz.api.education.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.education.entity.Education;

public record CreateEducationResponse(
        @Schema(description = "생성된 교육 PK", requiredMode = RequiredMode.REQUIRED)
        Long educationId
) {
    public static CreateEducationResponse from(Education education) {
        return new CreateEducationResponse(education.getId());
    }
}
