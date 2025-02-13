package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import org.cotato.csquiz.domain.generation.entity.SessionNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionNotificationRepository extends JpaRepository<SessionNotification, Long> {
    List<SessionNotification> findAllByDoneFalse();
}
