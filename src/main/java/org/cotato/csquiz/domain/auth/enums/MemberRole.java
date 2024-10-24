package org.cotato.csquiz.domain.auth.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    REFUSED("ROLE_REFUSED", "승인 거절 부원"),
    GENERAL("ROLE_GENERAL", "승인 대기 부원"),
    MEMBER("ROLE_MEMBER", "현재 활동 중인 부원"),
    OLD_MEMBER("ROLE_OM", "활동 후 종료한 부원"),
    ADMIN("ROLE_ADMIN", "운영진"),
    EDUCATION("ROLE_EDUCATION","교육팀"),
    OPERATION("ROLE_OPERATION", "운영지원팀");

    private final String key;
    private final String description;

    public static MemberRole fromKey(final String key) {
        return Arrays.stream(MemberRole.values())
                .filter(memberRole -> memberRole.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ENUM_NOT_RESOLVED));
    }
}
