package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;

public record AttendResponse(
        AttendanceStatus status,
        String message
) {
    public static AttendResponse from(AttendanceStatus status) {
        return new AttendResponse(
                status,
                status.getMessage()
        );
    }
}
