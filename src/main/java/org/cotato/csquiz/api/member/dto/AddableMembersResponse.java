package org.cotato.csquiz.api.member.dto;

import java.util.List;
import org.cotato.csquiz.domain.auth.entity.Member;

public record AddableMembersResponse(
        List<SearchedMemberInfo> memberInfos
) {
    public static AddableMembersResponse from(List<Member> members) {
        return new AddableMembersResponse(members.stream()
                .map(SearchedMemberInfo::from)
                .toList());
    }
}
