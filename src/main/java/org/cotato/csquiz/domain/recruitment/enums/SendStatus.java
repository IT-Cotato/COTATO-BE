package org.cotato.csquiz.domain.recruitment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SendStatus {
	NOT_SENT("안보냄"),
	FAIL("전송 실패"),
	SUCCESS("전송 성공");

	private final String description;
}
