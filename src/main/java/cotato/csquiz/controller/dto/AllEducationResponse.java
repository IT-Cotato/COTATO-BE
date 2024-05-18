package cotato.csquiz.controller.dto;

import cotato.csquiz.domain.entity.Education;

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
