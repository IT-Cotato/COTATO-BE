package org.cotato.csquiz.domain.attendance.repository;

import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
