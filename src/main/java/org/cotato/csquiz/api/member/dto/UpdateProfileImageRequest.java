package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileImageRequest(
        @NotNull
        MultipartFile image
) {
}
