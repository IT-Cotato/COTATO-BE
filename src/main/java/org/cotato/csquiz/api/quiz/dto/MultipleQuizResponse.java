package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.Quiz;
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
                (quiz.getS3Info() != null) ? quiz.getS3Info().getUrl() : null,
                choices
        );
    }
}
