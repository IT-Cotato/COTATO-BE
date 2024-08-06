package org.cotato.csquiz.domain.attendance.repository;

import java.util.List;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    @Query("select a from AttendanceRecord a where a.attendance in :attendances")
    List<AttendanceRecord> findAllByAttendanceIdsInQuery(@Param("attendances") List<Attendance> attendances);
}