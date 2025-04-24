package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.Quiz;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.education.entity.ShortAnswer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortQuizResponse {

    private Long id;
    private Integer number;
    private String question;
    private String image;
    private List<ShortAnswerResponse> shortAnswers = new ArrayList<>();

    public static ShortQuizResponse of(Quiz quiz, List<ShortAnswer> shortAnswers) {
        return new ShortQuizResponse(
                quiz.getId(),
                quiz.getNumber(),
                quiz.getQuestion(),
                (quiz.getS3Info() != null) ? quiz.getS3Info().getUrl() : null,
                shortAnswers.stream().map(ShortAnswerResponse::from).toList()
        );
    }
}
