package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import org.cotato.csquiz.domain.generation.entity.Session;

public record SessionListResponse(
        Long sessionId,
        Integer sessionNumber,
        String photoUrl,
        String description,
        Long generationId,
        ItIssue itIssue,
        Networking networking,
        CSEducation csEducation
) {
    public static SessionListResponse from(Session session) {
        return new SessionListResponse(
                session.getId(),
                session.getNumber(),
                (session.getPhotoS3Info() != null) ? session.getPhotoS3Info().getUploadUrl() : null,
                session.getDescription(),
                session.getGeneration().getId(),
                session.getItIssue(),
                session.getNetworking(),
                session.getCsEducation()
        );
    }
}
