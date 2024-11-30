package org.cotato.csquiz.domain.generation.service.component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SessionReader {

    private final SessionRepository sessionRepository;

    @Transactional(readOnly = true)
    public Session findById(final Long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
    }
}