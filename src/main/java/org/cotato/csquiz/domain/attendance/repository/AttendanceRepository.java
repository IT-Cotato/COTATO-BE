package org.cotato.csquiz.domain.attendance.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("select a from Attendance a where a.sessionId in :sessionIds")
    List<Attendance> findAllBySessionIdsInQuery(@Param("sessionIds") List<Long> sessionIds);

    Optional<Attendance> findBySessionId(Long sessionId);
}
