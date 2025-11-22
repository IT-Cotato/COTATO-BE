package org.cotato.csquiz.common.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	NO_PERMISSION_EXCEPTION(HttpStatus.FORBIDDEN, "NP-001", "권한이 없는 유저입니다."),
	CANNOT_ACCESS_OTHER_GENERATION(HttpStatus.FORBIDDEN, "NP-002", "해당 기수의 부원이 아닙니다."),

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
	INVALID_EMAIL(HttpStatus.BAD_REQUEST, "A-001", "유효하지 않은 이메일입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "A-002", "유효하지 않은 패스워드입니다."),
	INVALID_PHONE_NUMBER_PREFIX(HttpStatus.BAD_REQUEST, "A-003", "010으로 시작하지 않습니다."),
	INVALID_PHONE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "A-004", "문자열이 포함되어있습니다."),
	CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "A-101", "요청하신 코드가 일치하지 않습니다."),
	CODE_EXPIRED(HttpStatus.BAD_REQUEST, "A-102", "코드 유효 시간이 만료되었습니다."),
	EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "A-201", "해당 이메일이 존재하지 않습니다."),
	REQUEST_AGAIN(HttpStatus.NOT_FOUND, "A-202", "해당 이메일에 대한 코드가 존재하지 않습니다. 다시 요청 해주세요"),
	EMAIL_DUPLICATED(HttpStatus.CONFLICT, "A-301", "존재하는 이메일 입니다."),
	PHONE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "A-302", "존재하는 전화번호입니다."),

	// 정책 관련
	SHOULD_AGREE_POLICY(HttpStatus.BAD_REQUEST, "P-001", "필수 정책에는 반드시 동의해야합니다."),
	NOT_CHECKED_ALL_LEAVING_POLICIES(HttpStatus.BAD_REQUEST, "P-002", "모든 탈퇴 정책에 동의해야합니다."),
	ALREADY_POLICY_CHECK(HttpStatus.CONFLICT, "P-301", "이미 동의한 정책입니다."),

	//회원 관련
	ROLE_IS_NOT_MATCH(HttpStatus.BAD_REQUEST, "M-101", "해당 ROLE은 변경할 수 없습니다."),
	CANNOT_ACTIVE(HttpStatus.BAD_REQUEST, "M-102", "비활성화 상태의 부원만 활성화 가능합니다."),
	ROLE_IS_NOT_OLD_MEMBER(HttpStatus.BAD_REQUEST, "M-103", "해당 회원의 ROLE은 OLD_MEMBER가 아닙니다."),
	INVALID_MEMBER_STATUS(HttpStatus.BAD_REQUEST, "M-104", "해당 상태의 부원은 승인/거절할 수 없습니다."),
	CANNOT_CHANGE_DEV_ROLE(HttpStatus.BAD_REQUEST, "M-105", "개발팀은 OM으로 변경할 수 없습니다."),
	SAME_PASSWORD(HttpStatus.CONFLICT, "M-301", "이전과 같은 비밀번호로 변경할 수 없습니다."),

	// 기수 운영 (세션 -> 출석)
	INVALID_DATE(HttpStatus.BAD_REQUEST, "G-101", "시작날짜가 끝 날짜보다 뒤입니다"),
	OVERLAPPING_DATE(HttpStatus.BAD_REQUEST, "G-102", "기간이 겹치는 기수가 존재합니다"),
	GENERATION_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "G-201", "같은 숫자의 기수가 있습니다"),
	SESSION_DATE_NOT_FOUND(HttpStatus.NOT_FOUND, "G-202", "세션 날짜가 존재하지 않습니다"),

	//기수별 활동 부원 도메인
	GENERATION_MEMBER_EXIST(HttpStatus.CONFLICT, "GM-301", "일부 부원이 해당 기수에 활동부원입니다"),

	// 교육 도메인
	REGRADE_FAIL(HttpStatus.BAD_REQUEST, "E-201", "재채점 할 기록이 없습니다."),
	EDUCATION_DUPLICATED(HttpStatus.CONFLICT, "E-301", "이미 교육이 존재합니다"),
	EDUCATION_CLOSED(HttpStatus.BAD_REQUEST, "E-401", "CS 퀴즈가 닫혀 있습니다 먼저 교육 시작 버튼을 눌러주세요"),
	EDUCATION_STATUS_NOT_BEFORE(HttpStatus.BAD_REQUEST, "E-402", "이미 시작한 적이 있는 교육입니다."),
	MEMBER_CANT_ACCESS(HttpStatus.BAD_REQUEST, "E-403", "해당 멤버의 ROLE로 접근할 수 없습니다"),

	FILE_EXTENSION_FAULT(HttpStatus.BAD_REQUEST, "F-001", "해당 파일 확장자 명이 존재하지 않습니다."),
	FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "F-002", "파일이 비어있습니다"),

	//세션 사진
	SESSION_IMAGE_COUNT_MISMATCH(HttpStatus.BAD_REQUEST, "IM-101", "저장된 사진 수와 요청 사진 수가 다릅니다."),
	SESSION_ORDER_INVALID(HttpStatus.BAD_REQUEST, "IM-102", "입력한 순서는 유효하지 않습니다."),

	INVALID_ANSWER(HttpStatus.BAD_REQUEST, "Q-101", "객관식 문제는 숫자 형식의 값만 정답으로 추가할 수 있습니다."),
	CONTENT_IS_NOT_ANSWER(HttpStatus.BAD_REQUEST, "Q-201", "추가되지 않은 정답을 추가할 수 없습니다."),
	QUIZ_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "Q-301", "퀴즈 번호는 중복될 수 없습니다."),
	CHOICE_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "Q-302", "선지 번호는 중복될 수 없습니다"),
	CONTENT_IS_ALREADY_ANSWER(HttpStatus.BAD_REQUEST, "Q-303", "이미 정답인 답을 추가했습니다"),
	QUIZ_ACCESS_DENIED(HttpStatus.BAD_REQUEST, "Q-401", "해당 퀴즈는 아직 접근할 수 없습니다."),
	QUIZ_TYPE_NOT_MATCH(HttpStatus.BAD_REQUEST, "Q-402", "주관식 정답만 추가 가능합니다."),

	KING_MEMBER_EXIST(HttpStatus.CONFLICT, "K-301", "이미 킹킹 멤버가 존재합니다"),

	SUBJECT_INVALID(HttpStatus.BAD_REQUEST, "E-000", "교육 주제는 NULL이거나 비어있을 수 없습니다."),

	PROCESSING(HttpStatus.CONFLICT, "D-999", "해당 키의 요청은 아직 처리 중 입니다."),

	ALREADY_REPLY_CORRECT(HttpStatus.BAD_REQUEST, "R-301", "해당 사용자는 이미 정답 처리되었습니다."),

	LAST_QUIZ_SCORER_NOT_EXIST(HttpStatus.CONFLICT, "W-201", "아직 마지막 문제 득점자가 없습니다"),
	WINNER_EXIST(HttpStatus.CONFLICT, "W-301", "이미 우승자가 존재합니다"),

	//출석 관련 AT
	OFFLINE_ATTEND_FAIL(HttpStatus.BAD_REQUEST, "AT-101", "거리 부적합으로 인한 대면 출석 실패"),
	INVALID_ATTEND_TIME(HttpStatus.BAD_REQUEST, "AT-102", "시간 입력 범위가 잘못되었습니다."),
	INVALID_ATTEND_TYPE(HttpStatus.BAD_REQUEST, "AT-103", "해당 세션에 맞는 타입으로 출결 입력을 진행해주세요."),
	INVALID_LOCATION(HttpStatus.BAD_REQUEST, "AT-104", "위치 정보를 입력해주세요."),
	INVALID_RECORD_UPDATE(HttpStatus.BAD_REQUEST, "AT-105", "세션 타입에 맞게 출결 기록을 수정해주세요."),
	CANNOT_GET_EXCEL(HttpStatus.BAD_REQUEST, "AT-106", "출석 예정인 출결 기록은 다운 받을 수 없습니다."),
	INVALID_ATTEND_DEADLINE(HttpStatus.BAD_REQUEST, "AT-107", "출석 마감 정보를 입력해 주세요"),
	ALREADY_ATTEND(HttpStatus.CONFLICT, "AT-301", "이미 해당 타입으로 출석한 기록이 있습니다."),
	ATTENDANCE_RECORD_EXIST(HttpStatus.CONFLICT, "AT-302", "출석 기록이 존재해 출석을 끌 수 없습니다."),
	ATTENDANCE_NOT_OPEN(HttpStatus.BAD_REQUEST, "AT-401", "출석 시간이 아닙니다."),
	INVALID_ATTENDANCE_LIST(HttpStatus.BAD_REQUEST, "AT-402", "다른 기수의 출석이 같이 요청되었습니다."),

	//프로젝트 관련
	LOGO_IMAGE_EXIST(HttpStatus.CONFLICT, "PJ-301", "이미 로고 이미지가 존재합니다."),
	THUMBNAIL_IMAGE_EXIST(HttpStatus.CONFLICT, "PJ-302", "이미 썸네일 이미지가 존재합니다."),

	//프로필 이미지 관련
	PROFILE_IMAGE_NOT_EXIST(HttpStatus.BAD_REQUEST, "PI-401", "요청에 프로필 이미지가 존재하지 않습니다."),

	//모집 알림 관련
	INVALID_RECRUITMENT_INFO(HttpStatus.BAD_REQUEST, "RE-101", "모집 수정 정보를 작성하세요."),
	ALREADY_REQUEST_NOTIFICATION(HttpStatus.CONFLICT, "RE-301", "이미 모집 신청을 완료했습니다"),
	RECRUITMENT_INFO_COUNT_INVALID(HttpStatus.CONFLICT, "RE-302", "모집 정보 갯수가 없거나 2개 이상힙니디."),

	// 500 오류 -> 서버측에서 처리가 실패한 부분들
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-000", "예상치 못한 서버 내부 에러 발생"),
	WEBSOCKET_SEND_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "소캣 메세지 전송 실패"),
	IMAGE_PROCESSING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-002", "이미지 처리에 실패했습니다."),
	IMAGE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-003", "s3 이미지 삭제처리를 실패했습니다"),
	INTERNAL_SQL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-004", "SQL 관련 에러 발생"),
	ENUM_NOT_RESOLVED(HttpStatus.BAD_REQUEST, "S-005", "입력한 Enum이 존재하지 않습니다."),
	SCORER_LOCK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-006", "득점자 락 획득 과정에서 에러 발생"),
	IMAGE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-007", "로컬 이미지 변환에 실패했습니다"),
	SSE_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-008", "서버 이벤트 전송간 오류 발생"),
	WEBP_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-009", "webp 변환에 실패했습니다"),
	GUILD_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "S-010", "디스코드 서버를 찾지 못했습니다."),
	CHANNEL_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "S-011", "디스코드 채널을 찾지 못했습니다."),
	DISCORD_BUTTON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-012", "디스코드 버튼 이벤트 ID를 찾지 못했습니다."),
	EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-013", "이메일 전송에 실패했습니다."),
	FILE_GENERATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S-014", "엑셀 파일 생성에 실패했습니다."),
	EVENT_TYPE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S-015", "이벤트 처리 중 에러 발생"),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
