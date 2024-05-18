package org.cotato.csquiz.api.quiz.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AllQuizzesResponse {

    private List<MultipleQuizResponse> multiples;
    private List<ShortQuizResponse> shortQuizzes;
}
