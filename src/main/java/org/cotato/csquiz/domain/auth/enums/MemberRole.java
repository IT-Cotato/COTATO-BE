package org.cotato.csquiz.domain.auth.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    REFUSED("ROLE_REFUSED", "승인 거절 부원", 6),
    GENERAL("ROLE_GENERAL", "승인 대기 부원", 5),
    OLD_MEMBER("ROLE_OM", "활동 후 종료한 부원", 4),
    MEMBER("ROLE_MEMBER", "현재 활동 중인 부원", 3),
    EDUCATION("ROLE_EDUCATION", "교육팀", 2),
    OPERATION("ROLE_OPERATION", "운영지원팀", 2),
    ADMIN("ROLE_ADMIN", "운영진", 1);

    private static final Map<String, MemberRole> ROLE_KEY_MAP = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(MemberRole::getKey, Function.identity()));

    private final String key;
    private final String description;
    private final int priority;

    public static MemberRole fromKey(final String key) {
        MemberRole result = ROLE_KEY_MAP.get(key);
        if (result == null) {
            throw new IllegalArgumentException(String.format("요청한 key(%s)를 찾을 수 없습니다.", key));
        }
        return result;
    }

    public boolean isLower(MemberRole role) {
        return this.priority > role.priority;
    }
}
