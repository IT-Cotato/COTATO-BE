package org.cotato.csquiz.api.member.dto;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record MemberResponse(
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	Long memberId,
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	String name,
	@Schema(requiredMode = Schema.RequiredMode.REQUIRED)
	String backFourNumber,
	@Schema(requiredMode = RequiredMode.NOT_REQUIRED)
	MemberRole role,
	@Schema(requiredMode = RequiredMode.NOT_REQUIRED)
	MemberPosition position,
	@Schema(requiredMode = RequiredMode.REQUIRED)
	MemberStatus status,
	@Schema(requiredMode = RequiredMode.NOT_REQUIRED)
	Integer passedGenerationNumber
) {
	public static MemberResponse of(Member member, String backFourNumber) {
		return MemberResponse.builder()
			.memberId(member.getId())
			.name(member.getName())
			.backFourNumber(backFourNumber)
			.role(member.getRole())
			.position(member.getPosition())
			.status(member.getStatus())
			.passedGenerationNumber(member.getPassedGenerationNumber())
			.build();
	}
}
