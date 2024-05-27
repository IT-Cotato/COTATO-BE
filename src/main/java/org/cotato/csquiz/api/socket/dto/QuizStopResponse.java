package org.cotato.csquiz.api.socket.dto;

public record QuizStopResponse(
        String command,
        Long quizId
) {
    public static QuizStopResponse from(Long quizId) {
        return new QuizStopResponse(
                "stop",
                quizId
        );
    }
}
