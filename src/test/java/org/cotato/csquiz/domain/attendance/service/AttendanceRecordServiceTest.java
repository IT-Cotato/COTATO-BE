package org.cotato.csquiz.domain.attendance.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.enums.SessionType;
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
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;


    @Test
    void 세션_타입에_맞는_출결_기록_변경() {
        // given
        Session session = Session.builder().sessionType(SessionType.OFFLINE).build();
        Attendance attendance = Attendance.builder().session(session).build();
        AttendanceRecord attendanceRecord = AttendanceRecord.absentRecord(attendance, 1L);

        when(attendanceRepository.findById(any())).thenReturn(Optional.of(attendance));
        when(attendanceRecordRepository.findByMemberIdAndAttendanceId(any(), any())).thenReturn(Optional.of(attendanceRecord));

        // when
        attendanceRecordService.updateAttendanceRecord(1L, 1L, AttendanceResult.OFFLINE);

        // then
        Assertions.assertEquals(AttendanceResult.OFFLINE, attendanceRecord.getAttendanceResult());
    }
}