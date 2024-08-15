package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDate;
import lombok.Builder;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;

@Builder
public record AttendanceResponse(
        Long sessionId,
        Long attendanceId,
        String sessionTitle,
        LocalDate sessionDate,
        AttendanceOpenStatus openStatus
) {
}
