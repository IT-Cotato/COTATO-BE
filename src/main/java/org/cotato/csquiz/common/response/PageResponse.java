package org.cotato.csquiz.common.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(
	List<T> content,
	Boolean hasNext,
	int totalPages,
	long totalElements,
	int page,
	int size,
	Boolean isFirst,
	Boolean isLast
) {
	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.hasNext(),
			page.getTotalPages(),
			page.getTotalElements(),
			page.getNumber(),
			page.getSize(),
			page.isFirst(),
			page.isLast()
		);
	}
}
