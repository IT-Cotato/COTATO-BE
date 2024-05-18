package org.cotato.csquiz.api.education.dto;

import lombok.Getter;
import org.cotato.csquiz.domain.education.enums.EducationStatus;

@Getter
public class PatchStatusRequest {
    private Long educationId;
    private EducationStatus status;
}
