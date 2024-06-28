package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.generation.entity.SessionPhoto;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionPhotoResponse(
        @NotNull
        Long photoId
) {
        public static AddSessionPhotoResponse from(SessionPhoto sessionPhoto) {
                return new AddSessionPhotoResponse(sessionPhoto.getId());
        }
}
