package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.generation.entity.Session;

public record MemberAttendResponse(
        @Schema(description = "멤버 PK")
        Long memberId,
        @Schema(description = "세션 타이틀", example = "3주차 세션")
        String sessionTitle,
        @Schema(description = "세션 날짜")
        LocalDate sessionDate,
        @Schema(description = "출결 진행 여부", examples = {
                "CLOSED", "OPEN"
        })
        AttendanceOpenStatus isOpened,
        @Schema(description = "마감된 출석에 대한 출결 결과", nullable = true)
        AttendanceResult attendanceResult
) {
    public static MemberAttendResponse closedAttendanceResponse(Session session, AttendanceRecord attendanceRecord,
                                                                AttendanceOpenStatus openStatus) {
        return new MemberAttendResponse(
                attendanceRecord.getMemberId(),
                session.getTitle(),
                session.getSessionDate(),
                openStatus,
                attendanceRecord.getAttendanceResult()
        );
    }

    public static MemberAttendResponse openedAttendanceResponse(Session session, Long memberId,
                                                                AttendanceOpenStatus openStatus) {
        return new MemberAttendResponse(
                memberId,
                session.getTitle(),
                session.getSessionDate(),
                openStatus,
                null
        );
    }
}
