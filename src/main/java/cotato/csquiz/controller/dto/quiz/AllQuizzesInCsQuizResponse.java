package cotato.csquiz.controller.dto.quiz;

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
