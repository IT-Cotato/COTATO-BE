package org.cotato.csquiz.domain.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PolicyCategory {
	PERSONAL_INFORMATION("개인정보 관련된 정책"),
	LEAVING("회원 탈퇴 시 필요한 정책");

	private final String description;
}
