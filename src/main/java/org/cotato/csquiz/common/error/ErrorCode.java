package org.cotato.csquiz.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // Auth 일반적인 인증 문제 Auth JWT 토큰 관련 에러
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T-001", "이미 만료된 토큰입니다."),
    FILTER_EXCEPTION(HttpStatus.UNAUTHORIZED, "T-002", "필터 내부에러 발생"),
    JWT_FORM_ERROR(HttpStatus.UNAUTHORIZED, "T-003", "jwt 형식 에러 발생"),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "T-004", "해당 리프레시 토큰이 DB에 존재하지 않습니다."),
    REISSUE_FAIL(HttpStatus.UNAUTHORIZED, "T-005", "액세스 토큰 재발급 요청 실패"),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "T-006", "로그인에 실패했습니다."),

    // DTO 에서 발생하는 에러
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "I-001", "입력 값이 잘못되었습니다."),
    // 404 오류 -> 객체를 찾을 수 없는 문제
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "I-201", "해당 Entity를 찾을 수 없습니다."),

    // 회원 가입
    EMAIL_TYPE_ERROR(HttpStatus.BAD_REQUEST, "A-001", "구글, 네이버 형식으로 입력해주세요"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "A-002", "유효하지 않은 패스워드입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "A-003", "유효하지 않은 전화번호 입니다."),
    CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "A-101", "요청하신 코드가 일치하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "A-201", "해당 이메일이 존재하지 않습니다."), // 이게 소켓에 왜 필요한지, find 그 부분에서 발생해야할 예외일듯
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "A-301", "존재하는 이메일 입니다."),
    PHONE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "A-302", "존재하는 전화번호입니다."),

    //회원 관련
    ROLE_IS_NOT_MATCH(HttpStatus.BAD_REQUEST, "M-101", "해당 ROLE은 변경할 수 없습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "M-102", "비밀번호가 일치하지 않습니다."),
    ROLE_IS_NOT_OLD_MEMBER(HttpStatus.BAD_REQUEST, "M-103", "해당 회원의 ROLE은 OLD_MEMBER가 아닙니다."),
    SAME_PASSWORD(HttpStatus.CONFLICT, "M-301", "이전과 같은 비밀번호로 변경할 수 없습니다."),

    // 기수 운영 (세션 -> 출석)
    INVALID_DATE(HttpStatus.BAD_REQUEST, "G-101", "시작날짜가 끝 날짜보다 뒤입니다"),
    GENERATION_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "G-201", "같은 숫자의 기수가 있습니다"),

    // 교육 도메인
    REGRADE_FAIL(HttpStatus.BAD_REQUEST, "E-201", "재채점 할 기록이 없습니다."),
    EDUCATION_DUPLICATED(HttpStatus.CONFLICT, "E-301", "이미 교육이 존재합니다"),
    EDUCATION_CLOSED(HttpStatus.BAD_REQUEST, "E-401", "CS 퀴즈가 닫혀 있습니다 먼저 교육 시작 버튼을 눌러주세요"),
    EDUCATION_STATUS_NOT_BEFORE(HttpStatus.BAD_REQUEST, "E-402", "이미 시작한 적이 있는 교육입니다."),
    MEMBER_CANT_ACCESS(HttpStatus.BAD_REQUEST, "E-403", "해당 멤버의 ROLE로 접근할 수 없습니다"),

    INVALID_ANSWER(HttpStatus.BAD_REQUEST, "Q-101", "객관식 문제는 숫자 형식의 값만 정답으로 추가할 수 있습니다."),
    CONTENT_IS_NOT_ANSWER(HttpStatus.BAD_REQUEST, "Q-201", "추가되지 않은 정답을 추가할 수 없습니다."),
    QUIZ_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "Q-301", "퀴즈 번호는 중복될 수 없습니다."),
    CHOICE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "Q-302", "선지 번호는 중복될 수 없습니다"),
    CONTENT_IS_ALREADY_ANSWER(HttpStatus.BAD_REQUEST, "Q-303", "이미 정답인 답을 추가했습니다"),
    QUIZ_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "Q-401", "해당 퀴즈는 아직 접근할 수 없습니다."),
    QUIZ_TYPE_NOT_MATCH(HttpStatus.BAD_REQUEST, "Q-402", "주관식 정답만 추가 가능합니다."),

    SUBJECT_INVALID(HttpStatus.BAD_REQUEST, "E-000", "교육 주제는 NULL이거나 비어있을 수 없습니다."),

    PROCESSING(HttpStatus.CONFLICT, "D-999", "해당 키의 요청은 아직 처리 중 입니다."),

    ALREADY_REPLY_CORRECT(HttpStatus.BAD_REQUEST, "R-301", "해당 사용자는 이미 정답 처리되었습니다."),

    // 500 오류 -> 서버측에서 처리가 실패한 부분들
    WEBSOCKET_SEND_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "소캣 메세지 전송 실패"),
    IMAGE_PROCESSING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-002", "이미지 처리에 실패했습니다."),
    IMAGE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-003", "s3 이미지 삭제처리를 실패했습니다"),
    INTERNAL_SQL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-004", "SQL 관련 에러 발생"),
    ENUM_NOT_RESOLVED(HttpStatus.BAD_REQUEST, "S-005", "입력한 Enum이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
