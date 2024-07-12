package org.cotato.csquiz.api.session.dto;

import java.util.List;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;

public record SessionListResponse(
        Long sessionId,
        Integer sessionNumber,
        String title,
        List<SessionListImageInfoResponse> imageInfos,
        String description,
        Long generationId,
        SessionContents sessionContents
) {
    public static SessionListResponse of(Session session, List<SessionImage> sessionImages) {
        return new SessionListResponse(
                session.getId(),
                session.getNumber(),
                session.getTitle(),
                SessionListImageInfoResponse.from(sessionImages),
                session.getDescription(),
                session.getGeneration().getId(),
                session.getSessionContents()
        );
    }
}
