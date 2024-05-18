package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.entity.Quiz;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleQuizResponse {

    private Long id;
    private int number;
    private String question;
    private String image;
    private List<ChoiceResponse> choices = new ArrayList<>();

    public static MultipleQuizResponse from(Quiz quiz, List<ChoiceResponse> choices) {
        return new MultipleQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuestion(),
                quiz.getPhotoUrl(),
                choices
        );
    }
}
