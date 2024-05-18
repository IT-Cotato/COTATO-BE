package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.enums.ChoiceCorrect;
import lombok.Data;

@Data
public class CreateChoiceRequest {

    private String content;
    private Integer number;
    private ChoiceCorrect isAnswer;
}
