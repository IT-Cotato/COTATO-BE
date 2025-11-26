package org.cotato.csquiz.domain.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageUpdateStatus {
	UPDATE("새 이미지로 변경"),
	KEEP("유지"),
	DEFAULT("기본 이미지로 변경");

	private final String description;
}
