package org.cotato.csquiz.api.member.dto;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;

public record MemberInfo(
	Long memberId,
	String name,
	String email,
	String backFourNumber,
	MemberRole role,
	MemberPosition position
) {
	public static MemberInfo of(Member findMember, String backFourNumber) {
		return new MemberInfo(
			findMember.getId(),
			findMember.getName(),
			findMember.getEmail(),
			backFourNumber,
			findMember.getRole(),
			findMember.getPosition()
		);
	}
}
