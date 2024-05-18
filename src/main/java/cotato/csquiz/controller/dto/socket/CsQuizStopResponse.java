package cotato.csquiz.controller.dto.socket;

public record CsQuizStopResponse(
        String command,
        Long educationId
) {
    public static CsQuizStopResponse from(String command, Long educationId) {
        return new CsQuizStopResponse(
                command,
                educationId
        );
    }
}
