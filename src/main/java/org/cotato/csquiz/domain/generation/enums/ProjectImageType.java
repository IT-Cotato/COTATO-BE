package org.cotato.csquiz.domain.generation.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProjectImageType {
    LOGO("로고"),
    THUMBNAIL("썸네일"),
    DETAIL("상세");

    private final String description;
}
