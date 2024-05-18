package cotato.csquiz.controller.dto.session;

import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.domain.enums.ItIssue;
import cotato.csquiz.domain.enums.Networking;

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
                session.getPhotoUrl(),
                session.getDescription(),
                session.getGeneration().getId(),
                session.getItIssue(),
                session.getNetworking(),
                session.getCsEducation()
        );
    }
}
