package org.cotato.csquiz.api.project.dto;

import org.cotato.csquiz.domain.generation.entity.ProjectImage;
import org.cotato.csquiz.domain.generation.enums.ProjectImageType;

public record ProjectImageInfoResponse(
        Long imageId,
        String imageUrl,
        ProjectImageType projectImageType,
        Integer imageOrder
) {
        public static ProjectImageInfoResponse from(ProjectImage projectImage) {
            return new ProjectImageInfoResponse(
                    projectImage.getId(),
                    projectImage.getS3Info().getUrl(),
                    projectImage.getProjectImageType(),
                    projectImage.getImageOrder() != null ? projectImage.getImageOrder() : 0
            );
        }
}
