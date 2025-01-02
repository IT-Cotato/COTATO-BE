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

    REFUSED("ROLE_REFUSED", "승인 거절 부원"),
    GENERAL("ROLE_GENERAL", "승인 대기 부원"),
    MEMBER("ROLE_MEMBER", "현재 활동 중인 부원"),
    OLD_MEMBER("ROLE_OM", "활동 후 종료한 부원"),
    EDUCATION("ROLE_EDUCATION","교육팀"),
    OPERATION("ROLE_OPERATION", "운영지원팀"),
    MANAGER("ROLE_MANAGER", "운영 팀으로 활동하는 부원, 현재 교육팀과 운영지원팀"),
    ADMIN("ROLE_ADMIN", "운영진");

    private static final Map<String, MemberRole> ROLE_KEY_MAP = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(MemberRole::getKey, Function.identity()));

    private final String key;
    private final String description;

    public static MemberRole fromKey(final String key) {
        MemberRole result = ROLE_KEY_MAP.get(key);
        if (result == null) {
            throw new IllegalArgumentException(String.format("요청한 key(%s)를 찾을 수 없습니다.", key));
        }
        return result;
    }
}
