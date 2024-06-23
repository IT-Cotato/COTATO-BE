package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
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
        CSEducation csEducation,
        DevTalk devTalk
) {
}
