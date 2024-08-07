package org.cotato.csquiz.domain.attendance.service;

import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

public interface AttendClient {
    AttendanceType attendanceType();

    AttendResponse request(AttendanceParams params, Long memberId, Attendance attendance);
}
