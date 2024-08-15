package org.cotato.csquiz.domain.generation.repository;

import org.cotato.csquiz.domain.generation.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long>{
}
