package org.cotato.csquiz.domain.education.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EducationStatus {
	BEFORE("교육 시작 전"),
	ONGOING("교육 진행"),
	FINISHED("교육 종료");

	private final String description;
}
