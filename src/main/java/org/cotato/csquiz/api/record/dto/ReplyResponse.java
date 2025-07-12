package org.cotato.csquiz.api.record.dto;

public record ReplyResponse(
        boolean result
) {

    public static ReplyResponse from(Boolean isCorrect) {
        return new ReplyResponse(isCorrect);
    }
}
