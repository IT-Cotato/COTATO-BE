package org.cotato.csquiz.api.project.dto;

import java.util.List;

import org.cotato.csquiz.domain.generation.entity.Project;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.entity.ProjectMember;

public record ProjectDetailResponse(
	Long projectId,
	String name,
	String introduction,
	String githubUrl,
	String behanceUrl,
	String projectUrl,
	Long generationId,
	Integer generationNumber,
	List<ProjectImageInfoResponse> imageInfos,
	List<ProjectMemberInfoResponse> memberInfos
) {
	public static ProjectDetailResponse of(Project project, Integer generationNumber, List<ProjectImage> projectImages,
		List<ProjectMember> projectMembers) {
		return new ProjectDetailResponse(
			project.getId(),
			project.getName(),
			project.getIntroduction(),
			project.getGithubUrl(),
			project.getBehanceUrl(),
			project.getProjectUrl(),
			project.getGenerationId(),
			generationNumber,
			projectImages.stream()
				.map(ProjectImageInfoResponse::from)
				.toList(),
			projectMembers.stream()
				.map(ProjectMemberInfoResponse::from)
				.toList()
		);
	}
}
