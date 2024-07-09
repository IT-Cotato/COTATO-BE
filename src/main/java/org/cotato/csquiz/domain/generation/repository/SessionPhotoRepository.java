package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionPhotoRepository extends JpaRepository<SessionPhoto, Long> {
    List<SessionPhoto> findAllBySession(Session session);

    Optional<SessionPhoto> findFirstBySessionOrderByOrderDesc(Session session);
}
