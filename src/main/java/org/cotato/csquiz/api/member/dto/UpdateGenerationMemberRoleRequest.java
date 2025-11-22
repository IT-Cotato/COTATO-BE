package org.cotato.csquiz.api.member.dto;

import org.cotato.csquiz.domain.generation.enums.GenerationMemberRole;

import jakarta.validation.constraints.NotNull;

public record UpdateGenerationMemberRoleRequest(
	@NotNull(message = "역할을 입력해주세요")
	GenerationMemberRole role
) {
}
