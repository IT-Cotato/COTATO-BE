package org.cotato.csquiz.api.record.dto;

public record ReplyResponse(
        String result
) {

    public static ReplyResponse from(Boolean isCorrect) {
        return new ReplyResponse(isCorrect.toString());
    }
}
