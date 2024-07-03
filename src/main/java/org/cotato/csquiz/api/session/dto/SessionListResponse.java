package org.cotato.csquiz.api.session.dto;

import java.util.List;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Session;

public record SessionListResponse(
        Long sessionId,
        Integer sessionNumber,
        String title,
        List<SessionListPhotoInfoResponse> photoInfos,
        String description,
        Long generationId,
        SessionContents sessionContents
) {
    public static SessionListResponse from(Session session) {
        return new SessionListResponse(
                session.getId(),
                session.getNumber(),
                session.getTitle(),
                SessionListPhotoInfoResponse.from(session.getSessionPhotos()),
                session.getDescription(),
                session.getGeneration().getId(),
                session.getSessionContents()
        );
    }
}
