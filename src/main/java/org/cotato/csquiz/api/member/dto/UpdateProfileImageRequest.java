package org.cotato.csquiz.api.member.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record UpdateProfileImageRequest(
	@NotNull
	MultipartFile image
) {
}
