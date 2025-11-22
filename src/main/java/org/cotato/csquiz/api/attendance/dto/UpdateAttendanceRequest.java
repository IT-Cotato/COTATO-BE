package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.embedded.Location;

import jakarta.validation.constraints.NotNull;

public record UpdateAttendanceRequest(
	@NotNull
	Long attendanceId,
	Location location,
	@NotNull
	AttendanceDeadLineDto attendTime
) {
	public static UpdateAttendanceRequest of(Long attendanceId, Location location, AttendanceDeadLineDto attendTime) {
		return new UpdateAttendanceRequest(attendanceId, location, attendTime);
	}
}
