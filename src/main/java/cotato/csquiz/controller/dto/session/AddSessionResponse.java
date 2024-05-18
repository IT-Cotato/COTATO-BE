package cotato.csquiz.controller.dto.session;

import cotato.csquiz.domain.entity.Session;

public record AddSessionResponse(
        Long sessionId,
        Integer sessionNumber
) {
    public static AddSessionResponse from(Session session) {
        return new AddSessionResponse(
                session.getId(),
                session.getNumber()
        );
    }
}
