package org.cotato.csquiz.common.sse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SseAttendanceRepository {

	private final Map<Long, SseEmitter> attendances = new ConcurrentHashMap<>();

	public Optional<SseEmitter> findById(final Long memberId) {
		return Optional.ofNullable(attendances.get(memberId));
	}

	public void save(Long memberId, SseEmitter sseEmitter) {
		attendances.put(memberId, sseEmitter);
	}

	public void deleteById(Long memberId) {
		attendances.remove(memberId);
	}

	public boolean existsById(Long memberId) {
		return attendances.containsKey(memberId);
	}

	public List<SseEmitter> findAll() {
		return attendances.values().stream()
			.toList();
	}
}
