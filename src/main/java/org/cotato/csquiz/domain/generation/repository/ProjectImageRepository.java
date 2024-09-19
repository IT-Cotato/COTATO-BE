package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
    List<ProjectImage> findAllByProjectId(Long projectId);
    List<ProjectImage> findAllByProjectIdInAndProjectImageType(List<Long> projectIds, ProjectImageType projectImageType);

    boolean existsByProjectIdAndProjectImageType(Long projectId, ProjectImageType projectImageType);
}