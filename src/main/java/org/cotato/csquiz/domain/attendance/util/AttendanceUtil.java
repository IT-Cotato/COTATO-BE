package org.cotato.csquiz.domain.attendance.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.DeadLine;

public class AttendanceUtil {

    // 출석 시간에 따른 지각 여부 구분하기
    public static AttendanceStatus calculateAttendanceStatus(Attendance attendance, LocalDateTime attendTime){
        if (attendTime.isBefore(attendance.getAttendanceDeadLine())) {
            return AttendanceStatus.PRESENT;
        } if (attendTime.isBefore(attendance.getLateDeadLine())) {
            return AttendanceStatus.LATE;
        }
        return AttendanceStatus.ABSENT;
    }

    // 현재 시간을 기준으로 출석 open 상태를 반환한다.
    public static AttendanceOpenStatus getAttendanceStatus(Attendance attendance, LocalDateTime currentDateTime) {
        if (!isToday(attendance, currentDateTime) || !isStarted(currentDateTime.toLocalTime())) {
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

    private static boolean isToday(Attendance attendance, LocalDateTime currentDate) {
        return currentDate.toLocalDate().equals(attendance.getAttendanceDeadLine().toLocalDate());
    }

    private static boolean isStarted(LocalTime currentTime) {
        return currentTime.isAfter(DeadLine.ATTENDANCE_START_TIME.getTime());
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
