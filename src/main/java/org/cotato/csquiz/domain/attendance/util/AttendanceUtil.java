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

    // 현재 시간을 기준으로 출석이 열려있는지를 반환한다.
    public static AttendanceOpenStatus getAttendanceOpenStatus(LocalDateTime sessionStartTime, Attendance attendance, LocalDateTime currentDateTime) {
        if (currentDateTime.isBefore(sessionStartTime)) {
            return AttendanceOpenStatus.BEFORE;
        }

        if (currentDateTime.toLocalDate().isAfter(sessionStartTime.toLocalDate())) {
            return AttendanceOpenStatus.CLOSED;
        }

        if (currentDateTime.isAfter(sessionStartTime) && currentDateTime.isBefore(attendance.getAttendanceDeadLine())) {
            return AttendanceOpenStatus.OPEN;
        }

        if (currentDateTime.isAfter(attendance.getAttendanceDeadLine()) && currentDateTime.isBefore(attendance.getLateDeadLine())) {
            return AttendanceOpenStatus.LATE;
        }
        return AttendanceOpenStatus.ABSENT;
    }

    public static void validateAttendanceTime(LocalDateTime sessionStartTime, LocalTime attendDeadLine, LocalTime lateDeadLine) {
        if (!sessionStartTime.toLocalTime().isBefore(attendDeadLine)) {
            throw new AppException(ErrorCode.INVALID_ATTEND_TIME);
        }

        if (!attendDeadLine.isBefore(lateDeadLine)) {
            throw new AppException(ErrorCode.INVALID_ATTEND_TIME);
        }
    }
}
