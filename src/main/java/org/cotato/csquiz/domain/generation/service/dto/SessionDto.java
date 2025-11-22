package org.cotato.csquiz.domain.generation.service.dto;

import java.time.LocalDateTime;

import lombok.Builder;

import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.enums.SessionType;

@Builder
public record SessionDto(
	String title,
	String description,
	SessionType type,
	String placeName,
	String roadNameAddress,
	SessionContents sessionContents,
	LocalDateTime sessionDateTime
) {
}
