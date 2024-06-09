package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.QuizType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindMultipleQuizResponse extends QuizResponse {

    private List<ChoiceResponse> choices;

    private FindMultipleQuizResponse(Long id, Integer number, String question, String image,
                                     List<ChoiceResponse> choices) {
        super(id, number, QuizType.MULTIPLE_QUIZ, question, image);
        this.choices = choices;
    }

    public static FindMultipleQuizResponse from(Quiz quiz, List<ChoiceResponse> choices) {
        return new FindMultipleQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuestion(),
                (quiz.getS3Info() != null) ? quiz.getS3Info().getUploadUrl() : null,
                choices
        );
    }
}
