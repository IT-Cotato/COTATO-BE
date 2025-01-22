package org.cotato.csquiz.domain.attendance.service.component;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceReader {

    private final AttendanceRepository attendanceRepository;

    public Attendance findById(final Long id) {
        return attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 출석 정보를 찾을 수 없습니다."));
    }

    public Optional<Attendance> findBySessionIdWithPessimisticXLock(Long sessionId) {
        return attendanceRepository.findBySessionIdWithPessimisticXLock(sessionId);
    }

    public List<Attendance> getAllBySessions(final Collection<Session> sessions) {
        List<Long> sessionIds = sessions.stream().map(Session::getId).toList();
        return attendanceRepository.findAllBySessionIdsInQuery(sessionIds);
    }
}
