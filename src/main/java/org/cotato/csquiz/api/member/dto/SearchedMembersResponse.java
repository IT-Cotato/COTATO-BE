package org.cotato.csquiz.api.member.dto;

import java.util.List;

import org.cotato.csquiz.domain.auth.entity.Member;

public record SearchedMembersResponse(
	List<SearchedMemberInfo> memberInfos
) {
	public static SearchedMembersResponse from(List<Member> members) {
		return new SearchedMembersResponse(members.stream()
			.map(SearchedMemberInfo::from)
			.toList());
	}
}
