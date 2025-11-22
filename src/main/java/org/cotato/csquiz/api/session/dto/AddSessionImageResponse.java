package org.cotato.csquiz.api.session.dto;

import org.cotato.csquiz.domain.generation.entity.SessionImage;

public record AddSessionImageResponse(
	Long imageId,
	String imageUrl,
	Integer order
) {
	public static AddSessionImageResponse from(SessionImage sessionImage) {
		return new AddSessionImageResponse(sessionImage.getId(),
			sessionImage.getS3Info().getUrl(),
			sessionImage.getOrder());
	}
}
