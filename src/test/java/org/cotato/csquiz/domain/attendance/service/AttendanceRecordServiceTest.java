package org.cotato.csquiz.domain.attendance.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceRecordReader;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.enums.SessionType;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AttendanceRecordServiceTest {

    @InjectMocks
    private AttendanceRecordService attendanceRecordService;

    @Mock
    private AttendanceReader attendanceReader;

    @Mock
    private AttendanceRecordReader attendanceRecordReader;

    @Mock
    private SessionReader sessionReader;

    @Mock
    private MemberReader memberReader;

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @Test
    void 세션_타입에_맞는_출결_기록_변경() {
        // given
        Session session = Session.builder().sessionType(SessionType.OFFLINE).build();
        Attendance attendance = Attendance.builder().session(session).build();
        AttendanceRecord attendanceRecord = AttendanceRecord.absentRecord(attendance, 1L);
        Member member = Member.defaultMember("test", "test", "test", "test");

        when(attendanceReader.findById(any())).thenReturn(attendance);
        when(attendanceRecordReader.getByAttendanceAndMember(any(), any())).thenReturn(Optional.of(attendanceRecord));
        when(sessionReader.getByAttendance(attendance)).thenReturn(session);
        when(memberReader.findById(any())).thenReturn(member);

        // when
        attendanceRecordService.updateAttendanceRecord(1L, 1L, AttendanceResult.OFFLINE);

        // then
        Assertions.assertEquals(AttendanceResult.OFFLINE, attendanceRecord.getAttendanceResult());
    }
}