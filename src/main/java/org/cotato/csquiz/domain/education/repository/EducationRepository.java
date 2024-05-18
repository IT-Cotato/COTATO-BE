package org.cotato.csquiz.domain.education.repository;

import org.cotato.csquiz.domain.education.entity.Education;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    boolean existsBySessionId(Long sessionId);

    List<Education> findAllByGenerationId(Long generationId);
}
