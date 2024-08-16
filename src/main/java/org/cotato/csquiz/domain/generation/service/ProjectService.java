package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

        // 프로젝트의 세대 ID와 Project ID 리스트 추출
        List<Long> generationIds = projects.stream()
                .map(Project::getGenerationId)
                .distinct()
                .toList();

        List<Long> projectIds = projects.stream()
                .map(Project::getId)
                .toList();

        // 세대 번호와 로고 이미지를 한 번에 배치로 조회
        Map<Long, Integer> generationNumberMap = getGenerationNumbersByGenerationIds(generationIds);
        Map<Long, ProjectImage> projectImageMap = getProjectLogosByProjectIds(projectIds);

        // 각 프로젝트에 대해 응답 생성
        return projects.stream()
                .map(project -> projectSummaryResponse(project, generationNumberMap, projectImageMap))
                .toList();
    }

    private Map<Long, Integer> getGenerationNumbersByGenerationIds(List<Long> generationIds) {
        return generationRepository.findGenerationNumbersByGenerationIds(generationIds)
                .stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> (Integer) result[1]
                ));
    }

    private Map<Long, ProjectImage> getProjectLogosByProjectIds(List<Long> projectIds) {
        return projectImageRepository.findAllByProjectIdInAndProjectImageType(projectIds, ProjectImageType.LOGO)
                .stream()
                .collect(Collectors.toMap(ProjectImage::getProjectId, Function.identity()));
    }

    private ProjectSummaryResponse projectSummaryResponse(Project project, Map<Long, Integer> generationMap, Map<Long, ProjectImage> projectImageMap) {
        Integer generationNumber = generationMap.get(project.getGenerationId());
        ProjectImage logoImage = projectImageMap.get(project.getId());

        return ProjectSummaryResponse.of(project, generationNumber, logoImage);
    }
}
