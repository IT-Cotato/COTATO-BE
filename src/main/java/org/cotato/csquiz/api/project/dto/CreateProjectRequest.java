package org.cotato.csquiz.api.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record CreateProjectRequest(
        @NotNull
        Integer generationNumber,
        @NotNull
        String projectName,
        String projectIntroduction,
        String githubUrl,
        String behanceUrl,
        String projectUrl,
        @Valid
        List<ProjectMemberRequest> members,
        @Schema(name = "로고 이미지")
        MultipartFile logoImage,
        @Schema(name = "썸네일 이미지")
        MultipartFile thumbNailImage
) {
}
