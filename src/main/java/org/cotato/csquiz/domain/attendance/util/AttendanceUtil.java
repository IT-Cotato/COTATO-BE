package org.cotato.csquiz.domain.attendance.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.DeadLine;

public class AttendanceUtil {

    // 출석 시간에 따른 지각 여부 구분하기
    public static AttendanceStatus calculateAttendanceStatus(Attendance attendance, LocalDateTime attendTime){
        if (attendTime.isBefore(attendance.getAttendanceDeadLine())) {
            return AttendanceStatus.PRESENT;
        } else if (attendTime.isBefore(attendance.getLateDeadLine())) {
            return AttendanceStatus.LATE;
        }
        return AttendanceStatus.ABSENT;
    }

    // 현재 시간을 기준으로 출석 open 상태를 반환한다.
    public static AttendanceOpenStatus getAttendanceStatus(Attendance attendance, LocalTime currentTime) {
        if (!isToday(attendance) || !isStarted()) {
            return AttendanceOpenStatus.CLOSED;
        }

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

    private static boolean isToday(Attendance attendance) {
        return LocalDate.now().equals(attendance.getAttendanceDeadLine().toLocalDate());
    }

    private static boolean isStarted() {
        return LocalTime.now().isBefore(DeadLine.ATTENDANCE_START_TIME.getTime());
    }
}
