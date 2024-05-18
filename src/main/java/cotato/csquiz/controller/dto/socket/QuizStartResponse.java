package cotato.csquiz.controller.dto.socket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizStartResponse {

    private Long quizId;
    private String command;
}
