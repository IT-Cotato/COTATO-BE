package org.cotato.csquiz.api.project.dto;

import org.cotato.csquiz.domain.generation.enums.ProjectImageType;
import org.springframework.web.multipart.MultipartFile;

public record ProjectImageRequest(
	ProjectImageType imageType,
	MultipartFile image
) {
}
