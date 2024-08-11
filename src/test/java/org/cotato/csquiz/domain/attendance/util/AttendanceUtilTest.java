package org.cotato.csquiz.domain.attendance.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AttendanceUtilTest {

    @Test
    void 날짜가_다르면_출석이_닫혀있다() {
        //given
        Attendance attendance = Attendance.builder()
                .attendanceDeadLine(LocalDateTime.now())
                .lateDeadLine(LocalDateTime.now().plusMinutes(10))
                .session(Session.builder()
                        .build())
                .build();

        //when
        AttendanceOpenStatus attendanceStatus = AttendanceUtil.getAttendanceStatus(attendance,
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
        AttendanceOpenStatus attendanceStatus = AttendanceUtil.getAttendanceStatus(attendance, beforeTime);

        //then
        assertEquals(attendanceStatus, AttendanceOpenStatus.CLOSED);
    }

    @Test
    void 지각마감이_세션시작보다_빠를_수_없다() {
        //given
        LocalTime attendDeadline = LocalTime.of(18, 40, 0);
        LocalTime lateDeadline = LocalTime.of(19, 20, 0);

        //when, then
        assertThatThrownBy(() -> AttendanceUtil.validateAttendanceTime(attendDeadline, lateDeadline))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_ATTEND_TIME);
    }

    @DisplayName(value = "지각마감이 출석보다 빠르면 예외를 발생한다.")
    @Test
    void 지각마감보다_출석마감이_빠르다() {
        //given
        LocalTime attendDeadline = LocalTime.of(19, 40, 0);
        LocalTime lateDeadline = LocalTime.of(19, 20, 0);

        //when, then
        assertThatThrownBy(() -> AttendanceUtil.validateAttendanceTime(attendDeadline, lateDeadline))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_ATTEND_TIME);
    }

    @DisplayName(value = "지각 마감이 세션 종료보다 늦으면 예외를 발생한다.")
    @Test
    void 지각마감시간_검증_기능() {
        //given
        LocalTime attendDeadline = LocalTime.of(19, 40, 0);
        LocalTime lateDeadline = LocalTime.of(20, 20, 0);

        //when, then
        assertThatThrownBy(() -> AttendanceUtil.validateAttendanceTime(attendDeadline, lateDeadline))
                .isInstanceOf(AppException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_ATTEND_TIME);
    }
}