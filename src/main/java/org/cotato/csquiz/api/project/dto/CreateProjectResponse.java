package org.cotato.csquiz.api.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.cotato.csquiz.domain.generation.entity.Project;

public record CreateProjectResponse(
        @Schema(description = "생성된 프로젝트 PK")
        Long projectId
) {
    public static CreateProjectResponse from(Project createdProject) {
        return new CreateProjectResponse(createdProject.getId());
    }
}
