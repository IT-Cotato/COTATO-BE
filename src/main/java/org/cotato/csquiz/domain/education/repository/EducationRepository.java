package org.cotato.csquiz.domain.education.repository;

import java.util.List;

import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.enums.EducationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
	List<Education> findAllByGenerationId(Long generationId);

	List<Education> findAllByStatus(EducationStatus status);
}
