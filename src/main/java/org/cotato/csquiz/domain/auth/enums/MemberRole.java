package org.cotato.csquiz.domain.auth.enums;

import java.util.Arrays;
import lombok.Getter;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;

@Getter
public enum MemberRole {

    REFUSED("ROLE_REFUSED"),
    GENERAL("ROLE_GENERAL"),
    MEMBER("ROLE_MEMBER"),
    OLD_MEMBER("ROLE_OM"),
    ADMIN("ROLE_ADMIN"),
    EDUCATION("ROLE_EDUCATION"),
    SUPPORT("ROLE_SUPPORT");

    private final String key;

    MemberRole(String key) {
        this.key = key;
    }

    public static MemberRole fromKey(final String key) {
        return Arrays.stream(MemberRole.values())
                .filter(memberRole -> memberRole.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ENUM_NOT_RESOLVED));
    }
}
