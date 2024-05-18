package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.entity.Session;

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
