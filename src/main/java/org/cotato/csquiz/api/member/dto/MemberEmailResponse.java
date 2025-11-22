package org.cotato.csquiz.api.member.dto;

public record MemberEmailResponse(
	String email
) {
	public static MemberEmailResponse from(String email) {
		return new MemberEmailResponse(email);
	}
}
