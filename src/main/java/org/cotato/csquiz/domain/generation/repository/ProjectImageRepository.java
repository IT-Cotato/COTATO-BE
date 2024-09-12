package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
    List<ProjectImage> findByProjectIdOrderByImageOrderAsc(Long projectId);
    List<ProjectImage> findAllByProjectIdInAndProjectImageType(List<Long> projectIds, ProjectImageType projectImageType);
}
