package org.cotato.csquiz.domain.attendance.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    @Query("select a from AttendanceRecord a where a.attendanceId in :attendanceIds")
    List<AttendanceRecord> findAllByAttendanceIdsInQuery(@Param("attendanceIds") List<Long> attendanceIds);

    boolean existsByAttendanceIdAndMemberIdAndAttendanceType(Long attendanceId, Long memberId, AttendanceType attendanceType);

    boolean existsByAttendanceId(Long attendanceId);

    Optional<AttendanceRecord> findByMemberIdAndAttendanceId(Long memberId, Long attendanceId);

    @Query("select  a from AttendanceRecord a where a.attendanceId in :attendanceIds and a.memberId = :memberId")
    List<AttendanceRecord> findAllByAttendanceIdsInQueryAndMemberId(@Param("attendanceIds") List<Long> attendanceIds, @Param("memberId") Long memberId);
  
    List<AttendanceRecord> findAllByAttendanceId(Long attendanceId);

    List<AttendanceRecord> findAllByAttendanceIdAndMemberIdIn(Long id, List<Long> memberIds);
}
