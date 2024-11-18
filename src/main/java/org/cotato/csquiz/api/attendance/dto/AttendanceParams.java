package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDateTime;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordCreationType;

public interface AttendanceParams {

    AttendanceRecordCreationType attendanceType();

    Long attendanceId();

    LocalDateTime requestTime();
}
