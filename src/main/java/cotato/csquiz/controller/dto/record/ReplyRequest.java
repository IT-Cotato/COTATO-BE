package cotato.csquiz.controller.dto.record;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReplyRequest(
        @NotNull
        Long quizId,
        @NotNull
        Long memberId,
        @NotNull(message = "공백이 아닌 정답을 제출해주세요!")
        List<String> inputs
) {
}
