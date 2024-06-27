package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Session;

public record SessionListResponse(
        Long sessionId,
        Integer sessionNumber,
        String title,
        String photoUrl,
        String description,
        Long generationId,
        SessionContents sessionContents
) {
    public static SessionListResponse from(Session session) {
        return new SessionListResponse(
                session.getId(),
                session.getNumber(),
                session.getTitle(),
                (session.getPhotoS3Info() != null) ? session.getPhotoS3Info().getUrl() : null,
                session.getDescription(),
                session.getGeneration().getId(),
                session.getSessionContents()
        );
    }
}
