package org.cotato.csquiz.common.idempotency;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProcessStatus {

	PROCESSING("현재 해당 요청 처리 중"),
	SUCCESS("요청 완료");

	private final String description;
}
