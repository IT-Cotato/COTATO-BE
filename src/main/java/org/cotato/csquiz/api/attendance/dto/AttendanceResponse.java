package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record AttendanceResponse(
        Long attendanceId,
        String sessionTitle,
        LocalDate sessionDate,
        Boolean isOpened
) {
}
