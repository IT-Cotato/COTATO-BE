package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.generation.entity.AttendanceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AttendanceNotificationRepository extends JpaRepository<AttendanceNotification, Long> {
    @Query("SELECT an FROM AttendanceNotification an JOIN FETCH an.attendance WHERE an.done = false")
    List<AttendanceNotification> findAllByDoneFalse();

    void deleteAllByAttendance(Attendance attendance);
}
