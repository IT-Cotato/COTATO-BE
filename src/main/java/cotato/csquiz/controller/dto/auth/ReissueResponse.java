package cotato.csquiz.controller.dto.auth;

public record ReissueResponse(
        String accessToken
) {
    public static ReissueResponse from(String accessToken) {
        return new ReissueResponse(accessToken);
    }
}
