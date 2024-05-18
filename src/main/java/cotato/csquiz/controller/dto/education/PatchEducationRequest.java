package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.enums.EducationStatus;
import lombok.Data;

@Data
public class PatchEducationRequest {

    private Long educationId;
    private EducationStatus status;
}
