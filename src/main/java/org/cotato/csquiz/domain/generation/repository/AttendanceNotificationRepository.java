package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import org.cotato.csquiz.domain.generation.entity.AttendanceNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceNotificationRepository extends JpaRepository<AttendanceNotification, Long> {
    List<AttendanceNotification> findAllByDoneFalse();
}
