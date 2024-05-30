package org.cotato.csquiz.api.socket.dto;

public record QuizStopResponse(
        String command,
        Long quizId
) {
    public static final String STOP_COMMAND = "stop";

    public static QuizStopResponse from(Long quizId) {
        return new QuizStopResponse(
                STOP_COMMAND,
                quizId
        );
    }
}
