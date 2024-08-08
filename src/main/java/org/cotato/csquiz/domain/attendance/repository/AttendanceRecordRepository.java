package org.cotato.csquiz.domain.attendance.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    @Query("select a from AttendanceRecord a where a.attendance in :attendances")
    List<AttendanceRecord> findAllByAttendanceIdsInQuery(@Param("attendances") List<Attendance> attendances);

    boolean existsByAttendanceIdAndMemberIdAndAttendanceType(Long attendanceId, Long memberId, AttendanceType attendanceType);

    Optional<AttendanceRecord> findByIdAndAttendanceStatus(Long id, AttendanceStatus attendanceStatus);
}
