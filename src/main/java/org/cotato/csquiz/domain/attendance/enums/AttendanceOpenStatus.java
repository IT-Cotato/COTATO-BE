package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceOpenStatus {
	CLOSED("출결 입력 기간이 마감되었습니다."),
	OPEN("현재 출석 진행 중"),
	LATE("현재 출결 입력 시 지각"),
	ABSENT("현재 출결 입력 시 결석"),
	BEFORE("아직 출석 시작 전입니다.");

	private final String description;
}
