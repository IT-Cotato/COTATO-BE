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

    MEMBER("ROLE_MEMBER", "현재 활동 중인 부원", 1),
    MANAGER("ROLE_MANAGER", "운영 팀으로 활동하는 부원, 현재 교육팀과 운영지원팀", 2),
    ADMIN("ROLE_ADMIN", "운영진", 3),
    DEV("ROLE_DEV", "코테이토 개발팀", 4);

    private static final Map<String, MemberRole> ROLE_KEY_MAP = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(MemberRole::getKey, Function.identity()));

    private final String key;
    private final String description;
    private final int accessLevel;

    public static MemberRole fromKey(final String key) {
        MemberRole result = ROLE_KEY_MAP.get(key);
        if (result == null) {
            throw new IllegalArgumentException(String.format("요청한 key(%s)를 찾을 수 없습니다.", key));
        }
        return result;
    }

    public boolean canAccess(MemberRole role) {
        return this.accessLevel >= role.accessLevel;
    }
}
