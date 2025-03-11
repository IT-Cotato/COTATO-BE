package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.project.dto.CreateProjectRequest;
import org.cotato.csquiz.api.project.dto.CreateProjectResponse;
import org.cotato.csquiz.api.project.dto.ProjectDetailResponse;
import org.cotato.csquiz.api.project.dto.ProjectSummaryResponse;
import org.cotato.csquiz.domain.generation.entity.Generation;
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

    private final ProjectMemberService projectMemberService;
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
        Generation generation = generationRepository.findById(project.getGenerationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        return ProjectDetailResponse.of(project, generation.getNumber(), images, members);
    }

    @Transactional
    public List<ProjectSummaryResponse> getAllProjectSummaries() {

        List<Project> projects = projectRepository.findAll();

        List<Long> generationIds = projects.stream()
                .map(Project::getGenerationId)
                .distinct()
                .toList();

        List<Long> projectIds = projects.stream()
                .map(Project::getId)
                .toList();

        Map<Long, Integer> generationNumber = generationRepository.findAllByIdsInQuery(generationIds).stream()
                .collect(Collectors.toUnmodifiableMap(Generation::getId, Generation::getNumber));

        Map<Long, ProjectImage> projectImage = projectImageRepository.findAllByProjectIdInAndProjectImageType(
                        projectIds, ProjectImageType.LOGO).stream()
                .collect(Collectors.toUnmodifiableMap(ProjectImage::getProjectId, Function.identity()));

        return projects.stream()
                .map(project -> ProjectSummaryResponse.of(project, generationNumber.get(project.getGenerationId()),
                        projectImage.get(project.getId())))
                .toList();
    }

    @Transactional
    public CreateProjectResponse createProject(CreateProjectRequest request) {

        Generation generation = generationRepository.findByNumber(request.generationNumber())
                .orElseThrow(() -> new EntityNotFoundException("해당 번호의 기수를 찾을 수 없습니다."));

        Project createdProject = Project.builder()
                .name(request.projectName())
                .introduction(request.projectIntroduction())
                .githubUrl(request.githubUrl())
                .projectUrl(request.projectUrl())
                .behanceUrl(request.behanceUrl())
                .projectUrl(request.projectUrl())
                .generationId(generation.getId())
                .build();
        projectRepository.save(createdProject);
        projectMemberService.createProjectMember(createdProject, request.members());

        return CreateProjectResponse.from(createdProject);
    }
}
