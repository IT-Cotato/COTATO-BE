package org.cotato.csquiz.api.education.dto;

import lombok.Data;
import org.cotato.csquiz.domain.education.enums.EducationStatus;

@Data
public class PatchEducationRequest {

    private Long educationId;
    private EducationStatus status;
}
