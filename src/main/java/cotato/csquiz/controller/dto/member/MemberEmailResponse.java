package cotato.csquiz.controller.dto.member;

public record MemberEmailResponse(
        String email
) {
    public static MemberEmailResponse from(String email) {
        return new MemberEmailResponse(email);
    }
}
