package org.cotato.csquiz.api.policy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CheckPolicyRequest(
	@Schema(description = "체크할 정책 PK")
	@NotNull(message = "체크할 정책의 id를 입력해주세요.")
	Long policyId,
	@Schema(description = "정책 동의 여부")
	@NotNull(message = "정책 동의 여부를 입력해주세요.")
	Boolean isChecked
) {
}
