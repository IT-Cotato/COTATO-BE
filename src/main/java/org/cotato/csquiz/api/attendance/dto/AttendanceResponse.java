package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.LocalDateTime;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.entity.Session;

public record AttendanceResponse(
        @Schema(description = "출석 PK", requiredMode = RequiredMode.REQUIRED)
        Long attendanceId,
        @Schema(description = "출석 마감 시간", requiredMode = RequiredMode.REQUIRED)
        LocalDateTime attendanceDeadLine,
        @Schema(description = "지각 마감 시간", requiredMode = RequiredMode.REQUIRED)
        LocalDateTime lateDeadLine,
        @Schema(description = "출석 위경도",requiredMode = RequiredMode.NOT_REQUIRED)
        Location location,
        @Schema(description = "세션 PK", requiredMode = RequiredMode.REQUIRED)
        Long sessionId,
        @Schema(description = "출석 오픈 상태", requiredMode = RequiredMode.REQUIRED)
        AttendanceOpenStatus openStatus
) {
    public static AttendanceResponse of(Attendance attendance, Session session) {
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getAttendanceDeadLine(),
                attendance.getLateDeadLine(),
                attendance.getLocation(),
                attendance.getSessionId(),
                AttendanceUtil.getAttendanceOpenStatus(session.getSessionDateTime(), attendance, LocalDateTime.now())
        );
    }
}
