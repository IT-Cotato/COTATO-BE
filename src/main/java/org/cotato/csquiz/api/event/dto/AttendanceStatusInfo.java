package org.cotato.csquiz.api.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;

@Builder
public record AttendanceStatusInfo(
        @Schema(description = "출결 PK", nullable = true)
        Long attendanceId,
        @Schema(description = "오픈 상태: 존재하면 OPEN, 없으면 CLOSED")
        AttendanceOpenStatus openStatus
) {
}
