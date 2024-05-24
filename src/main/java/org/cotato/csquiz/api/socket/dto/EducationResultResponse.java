package org.cotato.csquiz.api.socket.dto;

import org.cotato.csquiz.domain.education.entity.Education;

public record EducationResultResponse(
        String command,
        Long educationId
) {
    public static EducationResultResponse of(String command, Education education) {
        return new EducationResultResponse(command, education.getId());
    }

}
