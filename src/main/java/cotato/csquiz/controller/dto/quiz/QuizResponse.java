package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.entity.ShortQuiz;
import cotato.csquiz.domain.enums.QuizType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {

    private Long id;
    private Integer number;
    private QuizType quizType;
    private String question;
    private String image;

    public static QuizResponse from(ShortQuiz shortQuiz) {
        return new QuizResponse(
                shortQuiz.getId(),
                shortQuiz.getNumber(),
                QuizType.SHORT_QUIZ,
                shortQuiz.getQuestion(),
                shortQuiz.getPhotoUrl()
        );
    }
}
