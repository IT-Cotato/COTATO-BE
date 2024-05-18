package cotato.csquiz.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberPosition {
    NONE("NONE"),
    BE("Back-end"),
    FE("Front-end"),
    DESIGN("Design"),
    PM("Product-Manager");

    private final String fullName;
}
