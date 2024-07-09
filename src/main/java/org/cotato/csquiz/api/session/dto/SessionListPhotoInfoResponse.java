package org.cotato.csquiz.api.session.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.cotato.csquiz.domain.generation.entity.SessionPhoto;

public record SessionListPhotoInfoResponse(
        Long photoId,
        String photoUrl,
        Integer order
) {
        public static SessionListPhotoInfoResponse from(SessionPhoto sessionPhoto) {
                return new SessionListPhotoInfoResponse(sessionPhoto.getId(),
                        sessionPhoto.getS3Info().getUrl(),
                        sessionPhoto.getOrder());
        }

        public static List<SessionListPhotoInfoResponse> from(List<SessionPhoto> sessionPhotos) {
                return sessionPhotos.stream()
                        .map(SessionListPhotoInfoResponse::from)
                        .toList();
        }
}
