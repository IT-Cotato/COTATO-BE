package org.cotato.csquiz.domain.generation.enums;

import lombok.Getter;

@Getter
public enum CSEducation {

    CS_ON("CS_EDUCATION_ON"),
    CS_OFF("CS_EDUCATION_OFF");

    private final String education;

    CSEducation(String education) {
        this.education = education;
    }
}
