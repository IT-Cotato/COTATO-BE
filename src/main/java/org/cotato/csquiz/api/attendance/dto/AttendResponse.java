package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;

public record AttendResponse(
        AttendanceResult status,
        String message
) {
    public static AttendResponse from(AttendanceResult status) {
        return new AttendResponse(
                status,
                status.getMessage()
        );
    }
}
