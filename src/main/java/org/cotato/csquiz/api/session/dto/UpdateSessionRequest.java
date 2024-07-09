package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import jakarta.validation.constraints.NotNull;

public record UpdateSessionRequest(
        @NotNull
        Long sessionId,
        String title,
        String description,
        @NotNull
        ItIssue itIssue,
        @NotNull
        Networking networking,
        @NotNull
        CSEducation csEducation,
        @NotNull
        DevTalk devTalk
) {
}
