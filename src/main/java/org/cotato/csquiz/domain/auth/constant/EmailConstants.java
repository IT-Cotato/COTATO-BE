package org.cotato.csquiz.domain.auth.constant;

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
    public static final String MEMBER_NAME_SUFFIX = "%s님의 ";
    public static final String MEMBER_POSITION_PREFIX = "포지션: %s" + "<br>";
    public static final String MEMBER_GENERATION_PREFIX = "합격기수: %s" + "기<br>";
    public static final String SIGNUP_SUCCESS_SUBJECT = "코테이토 가입 승인이 완료됐습니다.";
    public static final String SIGNUP_REJECT_SUBJECT = "코테이토 가입 승인이 거절됐습니다.";
    public static final String SIGNUP_SUCCESS_MESSAGE = "가입이 승인되었습니다<br>";
    public static final String SIGNUP_FAIL_MESSAGE = "가입이 거절되었습니다<br>";
    public static final String SIGNUP_MESSAGE_PREFIX = ""
            + "<div style=\"background-color: #f2f2f2; padding: 20px;\">"
            + "<div style=\"background-color: #ffffff; padding: 20px;\">"
            + "<h1 style=\"color: #336699; font-family: 'Arial', sans-serif; font-size: 24px;\">안녕하세요 IT연합동아리 코테이토입니다!</h1>"
            + "<br>";
    public static final String COTATO_HYPERLINK = "<br>"
            + "<a href=\"https://www.cotato.kr\" style=\"display: inline-block; background-color: #336699; color: #ffffff; padding: 10px 20px; font-family: 'Arial', sans-serif; font-size: 16px; text-decoration: none; border-radius: 5px;\">코테이토 방문하기</a>";
}
