package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.entity.Education;

public record CreateEducationResponse(
        Long educationId
) {
    public static CreateEducationResponse from(Education education) {
        return new CreateEducationResponse(education.getId());
    }
}
