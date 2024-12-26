package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;

public record AttendResponse(
        AttendanceResult result,
        String message
) {
    public static AttendResponse from(AttendanceResult result) {
        return new AttendResponse(
                result,
                result.getMessage()
        );
    }
}
