package org.cotato.csquiz.api.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.enums.SessionType;

public record AddSessionResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long sessionId,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Integer sessionNumber,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        SessionType sessionType
) {
    public static AddSessionResponse from(Session session) {
        return new AddSessionResponse(
                session.getId(),
                session.getNumber(),
                session.getSessionType()
        );
    }
}
