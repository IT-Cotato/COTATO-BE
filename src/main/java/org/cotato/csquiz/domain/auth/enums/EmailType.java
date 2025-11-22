package org.cotato.csquiz.domain.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailType {
	SIGNUP("$sign-up", "exist", "코드 일치 성공"),
	UPDATE_PASSWORD("$update-pwd", "exist", "코드 요청 후 검증 요청을 보내지 않음");

	private final String keyPrefix;
	private final String value;
	private final String description;
}
