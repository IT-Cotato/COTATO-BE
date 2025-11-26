package org.cotato.csquiz.api.record.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record ReplyRequest(
	@NotNull
	Long quizId,
	@NotNull
	Long memberId,
	@NotNull(message = "공백이 아닌 정답을 제출해주세요!")
	List<String> inputs
) {
}
