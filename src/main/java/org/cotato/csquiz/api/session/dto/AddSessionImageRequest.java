package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionImageRequest(

        @NotNull
        Long sessionId,
        @NotNull
        MultipartFile image,
        @NotNull
        Integer order
) {
}
