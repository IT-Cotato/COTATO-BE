package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.entity.Session;

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
