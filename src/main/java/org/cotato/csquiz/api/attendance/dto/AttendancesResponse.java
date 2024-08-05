package org.cotato.csquiz.api.attendance.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record AttendancesResponse(
        Long generationId,
        Long generationNumber,
        List<AttendanceResponse> attendances
) {
}
