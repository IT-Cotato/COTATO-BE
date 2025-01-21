package org.cotato.csquiz.api.education.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;

@Builder
public record EducationCountResponse(
        @Schema(description = "전체 교육 수", requiredMode = RequiredMode.REQUIRED)
        Long educationCount,
        @Schema(description = "전체 퀴즈 수", requiredMode = RequiredMode.REQUIRED)
        Long quizCount
) {
}
