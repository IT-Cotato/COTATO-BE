package org.cotato.csquiz.api.attendance.dto;

import java.util.List;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordResult;

public record UpdateAttendanceRecordRequest(
        List<UpdateAttendanceRecordInfoRequest> updateRecordInfos
) {
    public record UpdateAttendanceRecordInfoRequest(
            Long memberId,
            AttendanceRecordResult attendanceResult
    ) {

    }
}
