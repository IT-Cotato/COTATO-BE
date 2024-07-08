package org.cotato.csquiz.api.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePhoneNumberRequest(
        @NotNull(message = "전화번호를 입력해주세요.")
        @Size(min = 11, max = 11, message = "'-'없이 11자리의 전화번호를 입력해주세요.")
        String phoneNumber
) {
}
