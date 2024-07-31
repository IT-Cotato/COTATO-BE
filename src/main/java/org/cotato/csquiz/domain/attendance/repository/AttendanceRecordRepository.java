package org.cotato.csquiz.domain.attendance.repository;

import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
}
