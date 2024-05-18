package cotato.csquiz.controller.dto.session;

import cotato.csquiz.domain.entity.Session;

public record CsEducationOnSessionNumberResponse(
        Long sessionId,
        Integer sessionNumber
) {
    public static CsEducationOnSessionNumberResponse from(Session session) {
        return new CsEducationOnSessionNumberResponse(
                session.getId(),
                session.getNumber()
        );
    }
}
