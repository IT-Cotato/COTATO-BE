package org.cotato.csquiz.api.project.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateProjectImageRequest(
        Long projectId,
        MultipartFile logoImage,
        MultipartFile thumbNailImage
)  {
}
