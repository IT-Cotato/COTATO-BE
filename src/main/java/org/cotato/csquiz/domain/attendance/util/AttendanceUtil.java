package org.cotato.csquiz.domain.attendance.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.DeadLine;

public class AttendanceUtil {
    // 출석 시간에 따른 지각 여부 구분하기
    public static AttendanceResult calculateAttendanceStatus(LocalDateTime sessionDateTime, Attendance attendance,
                                                             LocalDateTime attendTime) {
        // 입력한 날짜와 세션 날짜가 다르거나, 시작 전이라면
        if (!attendTime.toLocalDate().equals(sessionDateTime.toLocalDate()) || attendTime.isBefore(sessionDateTime)) {
            throw new AppException(ErrorCode.ATTENDANCE_NOT_OPEN);
        }

        if (attendTime.isAfter(sessionDateTime) && attendTime.isBefore(attendance.getAttendanceDeadLine())) {
            return AttendanceResult.PRESENT;
        }
        if (attendTime.isBefore(attendance.getLateDeadLine())) {
            return AttendanceResult.LATE;
        }
        return AttendanceResult.ABSENT;
    }

    // 현재 시간을 기준으로 출석 open 상태를 반환한다.
    public static AttendanceOpenStatus getAttendanceOpenStatus(Attendance attendance, LocalDateTime currentDateTime) {
        if (currentDateTime.isBefore(DeadLine.sessionStartTime(attendance.getAttendanceDeadLine().toLocalDate()))) {
            return AttendanceOpenStatus.BEFORE;
        }

        if (currentDateTime.isAfter(DeadLine.sessionEndTime(attendance.getLateDeadLine().toLocalDate()))) {
            return AttendanceOpenStatus.CLOSED;
        }

        LocalTime currentTime = currentDateTime.toLocalTime();

        if (currentTime.isAfter(DeadLine.ATTENDANCE_START_TIME.getTime())
                && currentTime.isBefore(attendance.getAttendanceDeadLine().toLocalTime())) {
            return AttendanceOpenStatus.OPEN;
        }

        if (currentTime.isAfter(attendance.getAttendanceDeadLine().toLocalTime())
                && currentTime.isBefore(attendance.getLateDeadLine().toLocalTime())) {
            return AttendanceOpenStatus.LATE;
        }

        return AttendanceOpenStatus.ABSENT;
    }

    public static void validateAttendanceTime(LocalTime attendDeadLine, LocalTime lateDeadLine) {
        if (!DeadLine.ATTENDANCE_START_TIME.getTime().isBefore(attendDeadLine)) {
            throw new AppException(ErrorCode.INVALID_ATTEND_TIME);
        }

        if (!attendDeadLine.isBefore(lateDeadLine)) {
            throw new AppException(ErrorCode.INVALID_ATTEND_TIME);
        }

        if (!lateDeadLine.isBefore(DeadLine.ATTENDANCE_END_TIME.getTime())) {
            throw new AppException(ErrorCode.INVALID_ATTEND_TIME);
        }
    }
}
