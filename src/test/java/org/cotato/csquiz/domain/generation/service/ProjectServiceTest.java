package org.cotato.csquiz.domain.generation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.cotato.csquiz.api.project.dto.ProjectSummaryResponse;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Project;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.ProjectImageRepository;
import org.cotato.csquiz.domain.generation.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ProjectServiceTest {

	@InjectMocks
	private ProjectService projectService;

	@Mock
	private ProjectRepository projectRepository;

	@Mock
	private GenerationRepository generationRepository;

	@Mock
	private ProjectImageRepository projectImageRepository;

	@Test
	public void 프로젝트_목록_최신순_정렬() {
		// given
		Generation generation1 = spy(Generation.builder().number(100).build());
		Generation generation2 = spy(Generation.builder().number(200).build());
		when(generation1.getId()).thenReturn(1L);
		when(generation2.getId()).thenReturn(2L);

		List<Generation> generations = List.of(generation1, generation2);

		Project project1 = spy(Project.builder().generationId(generation1.getId()).build());
		Project project2 = spy(Project.builder().generationId(generation2.getId()).build());

		when(project1.getCreatedAt()).thenReturn(LocalDateTime.of(2021, 1, 1, 10, 0));
		when(project2.getCreatedAt()).thenReturn(LocalDateTime.of(2022, 1, 1, 10, 0));
		when(project1.getId()).thenReturn(1L);
		when(project2.getId()).thenReturn(2L);

		List<Project> projects = Arrays.asList(project1, project2);

		when(projectRepository.findAll()).thenReturn(projects);
		when(generationRepository.findAllByIdsInQuery(anyList())).thenReturn(generations);

		ProjectImage logo1 = ProjectImage.logoImage(S3Info.builder().url("logo").build(), project1.getId());

		ProjectImage logo2 = ProjectImage.logoImage(S3Info.builder().url("logo").build(), project2.getId());

		List<ProjectImage> images = List.of(logo1, logo2);
		when(projectImageRepository.findAllByProjectIdInAndProjectImageType(anyList(), eq(ProjectImageType.LOGO)))
			.thenReturn(images);

		// when
		List<ProjectSummaryResponse> summaries = projectService.getAllProjectSummaries();

		// then
		assertEquals(2, summaries.size());
		assertEquals(2L, summaries.get(0).projectId()); // project2
		assertEquals(1L, summaries.get(1).projectId()); // project1
	}
}
