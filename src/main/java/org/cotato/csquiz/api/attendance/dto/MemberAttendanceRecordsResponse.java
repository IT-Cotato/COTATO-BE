package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;

public record MemberAttendanceRecordsResponse(
        @Schema(description = "요청한 기수 PK", requiredMode = RequiredMode.REQUIRED)
        Long generationId,
        List<MemberAttendResponse> memberAttendResponses
) {
    public static MemberAttendanceRecordsResponse of(Long generationId, List<MemberAttendResponse> memberAttendResponses) {
        return new MemberAttendanceRecordsResponse(
                generationId,
                memberAttendResponses
        );
    }
}
