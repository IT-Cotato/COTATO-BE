package org.cotato.csquiz.domain.generation.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByGeneration(Generation generation);

    List<Session> findAllByGenerationId(Long generationId);

    List<Session> findAllByGenerationAndSessionContentsCsEducation(Generation generation, CSEducation csEducation);

    @Transactional(readOnly = true)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Session s WHERE s.id = :sessionId")
    Optional<Session> findByIdWithPessimisticXLock(@Param("sessionId") Long sessionId);
}
