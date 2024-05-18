package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.enums.EducationStatus;

public record FindEducationStatusResponse(
        EducationStatus status
) {
    public static FindEducationStatusResponse from(Education education) {
        return new FindEducationStatusResponse(education.getStatus());
    }
}
