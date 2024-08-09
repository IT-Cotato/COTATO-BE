package org.cotato.csquiz.domain.attendance.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.junit.jupiter.api.Test;

class AttendanceUtilTest {

    @Test
    void 날짜가_다르면_출석이_닫혀있다(){
        //given
        Attendance attendance = Attendance.builder()
                .attendanceDeadLine(LocalDateTime.now())
                .lateDeadLine(LocalDateTime.now().plusMinutes(10))
                .session(Session.builder()
                        .build())
                .build();

        //when
        AttendanceOpenStatus attendanceStatus = AttendanceUtil.getAttendanceOpenStatus(attendance,
                LocalDateTime.now().plusDays(1));

        //then
        assertEquals(attendanceStatus, AttendanceOpenStatus.CLOSED);
    }

    @Test
    void 기준시간_전이면_출석이_닫혀있다() {
        //given
        LocalDateTime attendanceDeadLine = LocalDateTime.now();

        Attendance attendance = Attendance.builder()
                .attendanceDeadLine(attendanceDeadLine)
                .lateDeadLine(attendanceDeadLine.plusMinutes(10))
                .session(Session.builder()
                        .build())
                .build();

        LocalDateTime beforeTime = attendanceDeadLine.minusMinutes(10);

        //when
        AttendanceOpenStatus attendanceStatus = AttendanceUtil.getAttendanceOpenStatus(attendance, beforeTime);

        //then
        assertEquals(attendanceStatus, AttendanceOpenStatus.CLOSED);
    }
}