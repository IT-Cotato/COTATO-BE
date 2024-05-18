package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.entity.Quiz;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortQuizResponse {

    private Long id;
    private Integer number;
    private String question;
    private String image;
    private List<ShortAnswerResponse> shortAnswers = new ArrayList<>();

    public static ShortQuizResponse from(Quiz quiz, List<ShortAnswerResponse> shortAnswers) {
        return new ShortQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuestion(),
                quiz.getPhotoUrl(),
                shortAnswers
        );
    }
}
