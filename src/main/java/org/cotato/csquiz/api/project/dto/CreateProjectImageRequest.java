package org.cotato.csquiz.api.project.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record CreateProjectImageRequest(
        Long projectId,
        MultipartFile logoImage,
        MultipartFile thumbNailImage,
        List<MultipartFile> detailImages
)  {
}
