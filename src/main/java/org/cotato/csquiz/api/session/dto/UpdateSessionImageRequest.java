package org.cotato.csquiz.api.session.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Valid
public record UpdateSessionImageRequest(
	@NotNull
	Long sessionId,
	MultipartFile image
) {
}
