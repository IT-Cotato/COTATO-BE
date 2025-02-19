package org.cotato.csquiz.domain.generation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cotato.csquiz.domain.attendance.entity.Attendance;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttendanceNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @Column(name = "is_done", nullable = false)
    private boolean done;

    @Builder
    public AttendanceNotification(Attendance attendance, boolean done) {
        this.attendance = attendance;
        this.done = done;
    }

    public void done() {
        this.done = true;
    }
}
