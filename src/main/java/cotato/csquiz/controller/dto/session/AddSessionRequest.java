package cotato.csquiz.controller.dto.session;

import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.domain.enums.ItIssue;
import cotato.csquiz.domain.enums.Networking;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionRequest(
        @NotNull
        Long generationId,
        MultipartFile sessionImage,
        @NotNull
        String description,
        ItIssue itIssue,
        Networking networking,
        CSEducation csEducation
) {
}
