package cotato.csquiz.domain.enums;

import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum MemberRole {

    REFUSED("ROLE_REFUSED"),
    GENERAL("ROLE_GENERAL"),
    MEMBER("ROLE_MEMBER"),
    OLD_MEMBER("ROLE_OM"),
    ADMIN("ROLE_ADMIN"),
    EDUCATION("ROLE_EDUCATION");

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
