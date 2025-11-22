package org.cotato.csquiz.api.project.dto;

import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.generation.entity.ProjectMember;

public record ProjectMemberInfoResponse(
	Long memberId,
	String name,
	MemberPosition position
) {
	public static ProjectMemberInfoResponse from(ProjectMember projectMember) {
		return new ProjectMemberInfoResponse(
			projectMember.getId(),
			projectMember.getName(),
			projectMember.getMemberPosition()
		);
	}
}
