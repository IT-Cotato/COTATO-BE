package org.cotato.csquiz.domain.generation.service.component;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SessionReader {

	private final SessionRepository sessionRepository;

	@Transactional(readOnly = true)
	public Session findById(final Long sessionId) {
		return sessionRepository.findById(sessionId)
			.orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
	}

	public Session findByIdWithPessimisticXLock(Long sessionId) {
		return sessionRepository.findByIdWithPessimisticXLock(sessionId)
			.orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
	}

	@Transactional(readOnly = true)
	public List<Session> findAllByGeneration(final Generation generation) {
		return sessionRepository.findAllByGenerationId(generation.getId());
	}

	@Transactional(readOnly = true)
	public List<Session> getAllByAttendances(List<Attendance> attendances) {
		List<Long> sessionIds = attendances.stream().map(Attendance::getSessionId).toList();
		return sessionRepository.findAllByIdIn(sessionIds);
	}

	@Transactional(readOnly = true)
	public Optional<Session> getByDate(LocalDate date) {
		return sessionRepository.findBySessionDate(date);
	}

	public Session getByAttendance(Attendance attendance) {
		return sessionRepository.findById(attendance.getSessionId())
			.orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
	}
}
