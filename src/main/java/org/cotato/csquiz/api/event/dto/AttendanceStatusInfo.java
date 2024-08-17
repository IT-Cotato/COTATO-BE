package org.cotato.csquiz.api.event.dto;

import lombok.Builder;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;

@Builder
public record AttendanceStatusInfo(
        Long attendanceId,
        AttendanceOpenStatus openStatus
) {
}
