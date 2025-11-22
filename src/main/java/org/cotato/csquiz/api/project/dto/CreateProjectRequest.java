package org.cotato.csquiz.api.project.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(
	@NotNull
	Integer generationNumber,
	@NotNull
	String projectName,
	String projectIntroduction,
	String githubUrl,
	String behanceUrl,
	String projectUrl,
	@Valid @NotNull List<ProjectMemberRequest> members
) {
}
