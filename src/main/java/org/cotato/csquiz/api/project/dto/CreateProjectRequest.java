package org.cotato.csquiz.api.project.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

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
