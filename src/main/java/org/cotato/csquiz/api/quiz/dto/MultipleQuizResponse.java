package org.cotato.csquiz.api.quiz.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipleQuizResponse {

    private Long id;
    private int number;
    private String question;
    private String image;
    private List<ChoiceResponse> choices = new ArrayList<>();

    public static MultipleQuizResponse of(MultipleQuiz quiz, List<Choice> choices) {
        return new MultipleQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuestion(),
                quiz.getImageUrl(),
                choices.stream().map(ChoiceResponse::forEducation).toList()
        );
    }
}
