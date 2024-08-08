package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDateTime;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

public interface AttendanceParams {

    AttendanceType attendanceType();

    Long attendanceId();

    LocalDateTime requestTime();
}
