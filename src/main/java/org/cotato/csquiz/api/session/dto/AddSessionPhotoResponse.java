package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.generation.entity.SessionPhoto;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionPhotoResponse(
        @NotNull
        Long photoId,
        @NotNull
        String photoUrl,
        @NotNull
        Integer order
) {
        public static AddSessionPhotoResponse from(SessionPhoto sessionPhoto) {
                return new AddSessionPhotoResponse(sessionPhoto.getId(),
                        sessionPhoto.getS3Info().getUrl(),
                        sessionPhoto.getOrder());
        }
}
