package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
    List<ProjectImage> findAllByProjectId(Long projectId);
    Optional<ProjectImage> findByProjectIdAndProjectImageType(Long projectId, ProjectImageType projectImageType);
}