package org.cotato.csquiz.domain.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlType {
    GITHUB("깃허브"),
    BEHANCE("비핸스"),
    BLOG("블로그"),
    OTHER("기타"),
    ;

    private final String description;
}
