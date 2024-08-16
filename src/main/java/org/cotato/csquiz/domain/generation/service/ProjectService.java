package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.project.dto.ProjectDetailResponse;
import org.cotato.csquiz.api.project.dto.ProjectSummaryResponse;
import org.cotato.csquiz.domain.generation.entity.Project;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.entity.ProjectMember;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.ProjectImageRepository;
import org.cotato.csquiz.domain.generation.repository.ProjectMemberRepository;
import org.cotato.csquiz.domain.generation.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectImageRepository projectImageRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final GenerationRepository generationRepository;

    @Transactional
    public ProjectDetailResponse getProjectDetail(Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("찾으려는 프로젝트가 존재하지 않습니다."));

        List<ProjectImage> images = projectImageRepository.findAllByProjectId(projectId);
        List<ProjectMember> members = projectMemberRepository.findAllByProjectId(projectId);
        Integer generationNumber = generationRepository.findGenerationNumberByGenerationId(project.getGenerationId());

        return ProjectDetailResponse.of(project, generationNumber, images, members);
    }

    @Transactional
    public List<ProjectSummaryResponse> getAllProjectSummaries(){

        List<Project> projects = projectRepository.findAll();

        return projects.stream().map(project -> {
            Optional<ProjectImage> logoImage = projectImageRepository.findByProjectIdAndProjectImageType(project.getId(), ProjectImageType.LOGO);
            Integer generationNumber = generationRepository.findGenerationNumberByGenerationId(project.getGenerationId());

            return ProjectSummaryResponse.of(project, generationNumber, logoImage.orElse(null));
        }).toList();
    }
}
