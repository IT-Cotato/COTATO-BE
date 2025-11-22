package org.cotato.csquiz.domain.attendance.repository;

import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	@Query("select a from Attendance a where a.sessionId in :sessionIds")
	List<Attendance> findAllBySessionIdsInQuery(@Param("sessionIds") List<Long> sessionIds);

	@Query("SELECT a FROM Attendance a WHERE DATE(a.attendanceDeadLine) = DATE(:time)")
	Optional<Attendance> findByAttendanceDeadLineDate(@Param("time") LocalDateTime time);

	Optional<Attendance> findBySessionId(Long sessionId);

	@Transactional(readOnly = true)
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Attendance a WHERE a.sessionId = :sessionId")
	Optional<Attendance> findBySessionIdWithPessimisticXLock(@Param("sessionId") Long sessionId);

	List<Attendance> findAllByIdIn(List<Long> attendanceIds);
}
