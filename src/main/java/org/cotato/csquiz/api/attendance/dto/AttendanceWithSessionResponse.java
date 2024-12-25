package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import lombok.Builder;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.generation.enums.SessionType;

@Builder
public record AttendanceWithSessionResponse(
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long sessionId,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        Long attendanceId,
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
        String sessionTitle,
        @Schema(requiredMode = RequiredMode.NOT_REQUIRED)
        LocalDateTime sessionDateTime,
        @Schema(requiredMode = RequiredMode.REQUIRED)
        SessionType sessionType
) {
}
