package org.cotato.csquiz.domain.generation.event;

import java.time.LocalDateTime;

import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.generation.entity.Session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceEventDto {

	private Session session;

	private Location location;

	private LocalDateTime attendanceDeadLine;

	private LocalDateTime lateDeadLine;
}
