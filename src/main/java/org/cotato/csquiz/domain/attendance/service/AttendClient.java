package org.cotato.csquiz.domain.attendance.service;

import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.generation.entity.Session;

public interface AttendClient {
	AttendanceType attendanceType();

	AttendResponse request(AttendanceParams params, Session session, Long memberId, Attendance attendance);
}
