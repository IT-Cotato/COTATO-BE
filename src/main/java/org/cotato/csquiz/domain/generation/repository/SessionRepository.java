package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByGeneration(Generation generation);

    List<Session> findAllByGenerationAndCsEducation(Generation generation, CSEducation csEducation);
}
