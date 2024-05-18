package org.cotato.csquiz.api.auth.dto;

public record ReissueResponse(
        String accessToken
) {
    public static ReissueResponse from(String accessToken) {
        return new ReissueResponse(accessToken);
    }
}
