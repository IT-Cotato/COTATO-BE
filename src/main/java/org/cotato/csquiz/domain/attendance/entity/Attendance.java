package org.cotato.csquiz.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest.AttendanceDeadLine;
import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.generation.entity.Session;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    private Location location;

    @Column(name = "session_id", nullable = false, unique = true)
    private Long sessionId;

    @Builder
    public Attendance(LocalDateTime startTime, LocalDateTime endTime, Location location, Session session) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.sessionId = session.getId();
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    public void updateDeadLine(LocalDate sessionDate, AttendanceDeadLine deadLine) {
        this.startTime = LocalDateTime.of(sessionDate, deadLine.startTime());
        this.endTime = LocalDateTime.of(sessionDate, deadLine.endTime());
    }
}
