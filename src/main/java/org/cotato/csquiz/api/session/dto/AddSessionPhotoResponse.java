package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;
import org.cotato.csquiz.domain.generation.entity.SessionPhoto;
import org.springframework.web.multipart.MultipartFile;

public record AddSessionPhotoResponse(
        Long photoId,
        String photoUrl,
        Integer order
) {
        public static AddSessionPhotoResponse from(SessionPhoto sessionPhoto) {
                return new AddSessionPhotoResponse(sessionPhoto.getId(),
                        sessionPhoto.getS3Info().getUrl(),
                        sessionPhoto.getOrder());
        }
}
