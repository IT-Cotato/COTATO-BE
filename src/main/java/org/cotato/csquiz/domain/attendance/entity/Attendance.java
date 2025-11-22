package org.cotato.csquiz.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(name = "attendance_deadline", nullable = false)
	private LocalDateTime attendanceDeadLine;

	@Column(name = "late_deadline", nullable = false)
	private LocalDateTime lateDeadLine;

	private Location location;

	@Column(name = "session_id", nullable = false, unique = true)
	private Long sessionId;

	@Builder
	public Attendance(LocalDateTime attendanceDeadLine, LocalDateTime lateDeadLine, Location location,
		Session session) {
		this.attendanceDeadLine = attendanceDeadLine;
		this.lateDeadLine = lateDeadLine;
		this.location = location;
		this.sessionId = session.getId();
	}

	public void updateLocation(Location location) {
		this.location = location;
	}

	public void updateDeadLine(LocalDateTime attendanceDeadLine, LocalDateTime lateDeadLine) {
		this.attendanceDeadLine = attendanceDeadLine;
		this.lateDeadLine = lateDeadLine;
	}
}
