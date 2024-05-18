package cotato.csquiz.controller.dto.education;

import cotato.csquiz.domain.entity.Quiz;

public record EducationIdOfQuizResponse(
        Long educationId
) {
    public static EducationIdOfQuizResponse from(Quiz quiz) {
        return new EducationIdOfQuizResponse(
                quiz.getEducation().getId()
        );
    }
}
