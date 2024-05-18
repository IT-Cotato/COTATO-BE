package cotato.csquiz.controller.dto.socket;

public record SocketTokenDto(
        String socketToken
) {
    public static SocketTokenDto from(String socketToken) {
        return new SocketTokenDto(
                socketToken
        );
    }
}
