package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.enums.EducationStatus;
import lombok.Getter;

@Getter
public class PatchStatusRequest {
    private Long educationId;
    private EducationStatus status;
}
