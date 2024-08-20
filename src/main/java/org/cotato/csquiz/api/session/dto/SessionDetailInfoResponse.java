package org.cotato.csquiz.api.session.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record SessionDetailInfoResponse(
        Long sessionId,
        LocalTime attendanceDeadLine,
        LocalTime lateDeadLine
) {
    public static SessionDetailInfoResponse of(Long sessionId, LocalDateTime attendanceDeadLine, LocalDateTime lateDeadLine) {
        return new SessionDetailInfoResponse(
                sessionId,
                attendanceDeadLine.toLocalTime(),
                lateDeadLine.toLocalTime()
        );
    }
}
