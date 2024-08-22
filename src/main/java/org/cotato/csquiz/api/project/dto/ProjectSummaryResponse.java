package org.cotato.csquiz.api.project.dto;

import org.cotato.csquiz.domain.generation.entity.Project;
import org.cotato.csquiz.domain.generation.entity.ProjectImage;

public record ProjectSummaryResponse(
        Long projectId,
        String name,
        String introduction,
        Long generationId,
        Integer generationNumber,
        String logoUrl
) {
    public static ProjectSummaryResponse of(Project project, Integer generationNumber, ProjectImage projectImage) {
        return new ProjectSummaryResponse(
                project.getId(),
                project.getName(),
                project.getIntroduction(),
                project.getGenerationId(),
                generationNumber,
                projectImage != null ? projectImage.getS3Info().getUrl() : null
        );
    }
}
