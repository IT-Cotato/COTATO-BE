package org.cotato.csquiz.api.quiz.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuizzesRequest {

    private List<CreateMultipleQuizRequest> multiples = new ArrayList<>();
    private List<CreateShortQuizRequest> shortQuizzes = new ArrayList<>();
}
