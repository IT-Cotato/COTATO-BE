package org.cotato.csquiz.api.project.dto;

import org.cotato.csquiz.domain.auth.enums.MemberPosition;

import jakarta.validation.constraints.NotNull;

public record ProjectMemberRequest(
	@NotNull
	String name,
	@NotNull
	MemberPosition position
) {
}
