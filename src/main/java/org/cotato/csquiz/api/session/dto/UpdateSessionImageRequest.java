package org.cotato.csquiz.api.session.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Valid
public record UpdateSessionImageRequest(
        @NotNull
        Long sessionId,
        MultipartFile image
) {
}
