package org.cotato.csquiz.api.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.entity.Session;

public record MemberAttendResponse(
        @Schema(description = "세션 PK")
        Long sessionId,
        @Schema(description = "출석 PK")
        Long attendanceId,
        @Schema(description = "멤버 PK")
        Long memberId,
        @Schema(description = "세션 타이틀", example = "3주차 세션")
        String sessionTitle,
        @Schema(description = "세션 날짜")
        LocalDateTime sessionDateTime,
        @Schema(description = "출결 진행 여부", examples = {
                "CLOSED", "OPEN"
        })
        AttendanceOpenStatus openStatus,
        @Schema(description = "출결 형식", nullable = true)
        AttendanceType attendanceType,
        @Schema(description = "마감된 출석에 대한 출결 결과", nullable = true)
        AttendanceResult result
) {
    public static MemberAttendResponse unrecordedAttendance(Session session, Attendance attendance, Long memberId) {
        return new MemberAttendResponse(
                session.getId(),
                attendance.getId(),
                memberId,
                session.getTitle(),
                session.getSessionDateTime(),
                AttendanceUtil.getAttendanceOpenStatus(session.getSessionDateTime(), attendance, LocalDateTime.now()),
                null,
                null
        );
    }

    public static MemberAttendResponse recordedAttendance(Session session, Attendance attendance,
                                                          AttendanceRecord attendanceRecord) {
        return new MemberAttendResponse(
                session.getId(),
                attendanceRecord.getAttendanceId(),
                attendanceRecord.getMemberId(),
                session.getTitle(),
                session.getSessionDateTime(),
                AttendanceUtil.getAttendanceOpenStatus(session.getSessionDateTime(), attendance, LocalDateTime.now()),
                attendanceRecord.getAttendanceType(),
                attendanceRecord.getAttendanceResult()
        );
    }
}
