package org.cotato.csquiz.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JoinRequest(
        @Email(message = "올바른 형식의 메일 주소를 입력해주세요.")
        @NotBlank(message = "공백이 아닌 올바른 형식의 메일 주소를 입력해주세요.")
        String email,
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자리여야합니다.")
        @NotNull(message = "비밀번호를 입력해주세요.")
        String password,
        @NotBlank(message = "올바른 형식의 이름을 입력해주세요.")
        String name,
        @NotNull(message = "전화번호를 입력해주세요.")
        @Size(min = 11, max = 11, message = "'-'없이 11자리의 전화번호를 입력해주세요.")
        String phoneNumber
) {
}
