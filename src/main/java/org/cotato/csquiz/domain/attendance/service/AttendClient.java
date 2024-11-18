package org.cotato.csquiz.domain.attendance.service;

import java.time.LocalDateTime;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordCreationType;

public interface AttendClient {
    AttendanceRecordCreationType attendanceType();

    AttendResponse request(AttendanceParams params, LocalDateTime sessionStartTime, Long memberId, Attendance attendance);
}
