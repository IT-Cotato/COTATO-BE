package org.cotato.csquiz.api.session.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record AddSessionImageRequest(

	@NotNull
	Long sessionId,
	@NotNull
	MultipartFile image,
	@NotNull
	Integer order
) {
}
