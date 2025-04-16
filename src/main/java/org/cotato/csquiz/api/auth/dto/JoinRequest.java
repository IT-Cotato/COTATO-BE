package org.cotato.csquiz.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import org.cotato.csquiz.api.policy.dto.CheckPolicyRequest;
import org.cotato.csquiz.common.validator.Password;
import org.cotato.csquiz.common.validator.Phone;

@Builder
public record JoinRequest(
        @Email(message = "올바른 형식의 메일 주소를 입력해주세요.")
        @NotBlank(message = "공백이 아닌 올바른 형식의 메일 주소를 입력해주세요.")
        String email,
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자리여야합니다.")
        @Password(message = "비밀번호는 영문, 숫자, 특수문자를 포함해야합니다.")
        String password,
        @NotBlank(message = "올바른 형식의 이름을 입력해주세요.")
        String name,
        @NotNull(message = "전화번호를 입력해주세요.")
        @Phone(message = "전화번호는 010으로 시작하는 11자리여야합니다.")
        String phoneNumber,

        @Schema(description = "동의 표시한 정책 목록")
        @NotNull
        List<CheckPolicyRequest> policies
) {
}
