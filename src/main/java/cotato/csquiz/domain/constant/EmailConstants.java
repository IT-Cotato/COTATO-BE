package cotato.csquiz.domain.constant;

public class EmailConstants {

    public static final String SENDER_EMAIL = "itcotato@gmail.com";
    public static final String SENDER_PERSONAL = "IT연합동아리 코테이토";
    public static final String SIGNUP_SUBJECT = "안녕하세요! 코테이토 회원 가입 인증 코드입니다.";
    public static final String PASSWORD_SUBJECT = "안녕하세요! 코테이토 비밀번호 찾기 인증 코드입니다.";
    public static final String MESSAGE_PREFIX = ""
            + "<div style=\"background-color: #f2f2f2; padding: 20px;\">"
            + "<div style=\"background-color: #ffffff; padding: 20px;\">"
            + "<h1 style=\"color: #336699; font-family: 'Arial', sans-serif; font-size: 24px;\">코테이토 인증 코드 입니다!</h1>"
            + "<br>"
            + "<p style=\"font-family: 'Arial', sans-serif; font-size: 16px;\">CODE: <strong>";
    public static final String MESSAGE_SUFFIX = ""
            + "</strong></p>"
            + "<br>"
            + "<h3 style=\"color: #336699; font-family: 'Arial', sans-serif; font-size: 18px;\">10분 안에 입력 부탁드립니다. 감사합니다!</h3>"
            + "</div>"
            + "</div>";
}
