package cotato.csquiz.controller.dto.record;

public record ReplyResponse(
        String result
) {

    public static ReplyResponse from(Boolean isCorrect) {
        return new ReplyResponse(isCorrect.toString());
    }
}
