package org.cotato.csquiz.api.quiz.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.ShortQuiz;

@Data
@AllArgsConstructor
@Builder
public class AllQuizzesResponse {

    private List<MultipleQuizResponse> multiples;
    private List<ShortQuizResponse> shortQuizzes;
}
