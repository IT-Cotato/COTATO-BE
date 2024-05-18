package org.cotato.csquiz.api.quiz.dto;

import java.util.List;

public record AllQuizzesInCsQuizResponse(
        List<CsAdminQuizResponse> quizzes
) {

    public static AllQuizzesInCsQuizResponse from(List<CsAdminQuizResponse> quizzes) {
        return new AllQuizzesInCsQuizResponse(
                quizzes
        );
    }
}
