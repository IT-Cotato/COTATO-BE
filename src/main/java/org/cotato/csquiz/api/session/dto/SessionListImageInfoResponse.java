package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.entity.SessionImage;

public record SessionListImageInfoResponse(
        Long imageId,
        String imageUrl,
        Integer order
) {
        public static SessionListImageInfoResponse from(SessionImage sessionImage) {
                return new SessionListImageInfoResponse(sessionImage.getId(),
                        sessionImage.getS3Info().getUrl(),
                        sessionImage.getOrder());
        }
}
