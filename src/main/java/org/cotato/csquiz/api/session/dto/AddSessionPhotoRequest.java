package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionPhotoRequest(

        @NotNull
        Long sessionId,
        @NotNull
        MultipartFile sessionImage
) {
}
