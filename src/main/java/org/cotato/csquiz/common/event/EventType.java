package org.cotato.csquiz.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

	APPROVE_MEMBER("부원 가입 승인"),
	REJECT_MEMBER("부원 가입 거절"),
	SESSION_IMAGE_UPDATE("세션 이미지 업데이트"),
	ATTENDANCE_CREATE("출석 생성"),
	;

	private final String description;
}
