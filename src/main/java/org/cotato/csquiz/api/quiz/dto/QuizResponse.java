package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.ShortQuiz;
import org.cotato.csquiz.domain.education.enums.QuizType;
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
                (shortQuiz.getS3Info() != null) ? shortQuiz.getS3Info().getUploadUrl() : null
        );
    }
}
