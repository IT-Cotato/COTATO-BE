package cotato.csquiz.controller.dto.auth;

public record FindPasswordResponse(
        String accessToken
) {
    public static FindPasswordResponse from(String accessToken) {
        return new FindPasswordResponse(accessToken);
    }
}
