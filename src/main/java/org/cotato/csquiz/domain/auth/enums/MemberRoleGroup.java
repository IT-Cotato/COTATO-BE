package org.cotato.csquiz.domain.auth.enums;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRoleGroup {

    ACTIVE_MEMBERS("현재 활동 중인 멤버", List.of(MemberRole.MEMBER, MemberRole.ADMIN, MemberRole.MANAGER, MemberRole.DEV)),
    CLIENTS("교육 중 문제 풀이가 가능한 멤버", List.of(MemberRole.MEMBER, MemberRole.MANAGER, MemberRole.ADMIN, MemberRole.DEV)),
    MANAGERS("교육 진행 관리자", List.of(MemberRole.MANAGER, MemberRole.ADMIN, MemberRole.DEV));

    private final String description;
    private final List<MemberRole> roles;

    public static boolean hasRole(MemberRoleGroup group, MemberRole role) {
        return group.getRoles().contains(role);
    }
}
