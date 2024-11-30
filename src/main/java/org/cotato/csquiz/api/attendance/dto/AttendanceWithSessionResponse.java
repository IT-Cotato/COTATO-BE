package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.Builder;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;

@Builder
public record AttendanceWithSessionResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long sessionId,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long attendanceId,
        String sessionTitle,
        LocalDateTime sessionDateTime,
        AttendanceOpenStatus openStatus
) {
}
