package org.cotato.csquiz.domain.generation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceRecordReader;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SessionServiceTest {
    @Mock
    private SessionReader sessionReader;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private AttendanceReader attendanceReader;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceRecordReader attendanceRecordReader;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void 세션_날짜_수정시_출결_기록이_존재하면_예외() {
        //given
        Long sessionId = 1L;
        LocalDateTime oldSessionDateTime = LocalDateTime.of(2025, 2, 1, 10, 0);
        LocalDateTime newSessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0); // 변경된 날짜

        UpdateSessionRequest request = mockOnlineUpdateSessionRequest(sessionId, newSessionDateTime);
        Session session = mockSession(sessionId, oldSessionDateTime);
        Attendance attendance = mockAttendance();

        when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
        when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(Optional.of(attendance));
        when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(true);

        // when & then
        assertThrows(AppException.class, () -> sessionService.updateSession(request));
    }

    @Test
    void 출석_삭제시_출결_기록이_존재하면_예외() {
        //given
        Long sessionId = 1L;
        LocalDateTime sessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0); //변경X 날짜

        UpdateSessionRequest request = mockNoAttendUpdateSessionRequest(sessionId, sessionDateTime);
        Session session = mockSession(sessionId, sessionDateTime);
        Attendance attendance = mockAttendance();

        when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
        when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(Optional.of(attendance));
        when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(true);

        // when & then
        assertThrows(AppException.class, () -> sessionService.updateSession(request));
    }

    @Test
    void 출결_기록이_존재하지_않으면_수정_가능() {
        //given
        Long sessionId = 1L;
        LocalDateTime oldSessionDateTime = LocalDateTime.of(2025, 2, 1, 10, 0);
        LocalDateTime newSessionDateTime = LocalDateTime.of(2025, 2, 2, 10, 0); // 변경된 날짜

        UpdateSessionRequest request = mockOnlineUpdateSessionRequest(sessionId, newSessionDateTime);
        Session session = mockSession(sessionId, oldSessionDateTime);
        Attendance attendance = mockAttendance();

        when(sessionReader.findByIdWithPessimisticXLock(sessionId)).thenReturn(session);
        when(attendanceReader.findBySessionIdWithPessimisticXLock(sessionId)).thenReturn(
                Optional.of(attendance)); // 출결 기록 없음
        when(attendanceRecordReader.isAttendanceRecordExist(attendance)).thenReturn(false);

        // when
        sessionService.updateSession(request);

        //then
        verify(sessionRepository).save(session);
        verify(attendanceRepository).save(Mockito.any(Attendance.class));
    }

    private UpdateSessionRequest mockOnlineUpdateSessionRequest(Long sessionId, LocalDateTime sessionDateTime) {
        return new UpdateSessionRequest(sessionId, "New Title", "New Description",
                sessionDateTime, "New Place", "도로 명 주소",
                Location.location(0.0, 0.0),
                attendanceDeadLineDto(sessionDateTime.plusMinutes(1), sessionDateTime.plusMinutes(2)),
                true, false, ItIssue.IT_ON, Networking.NW_ON, CSEducation.CS_ON, DevTalk.DEVTALK_ON);
    }

    private UpdateSessionRequest mockNoAttendUpdateSessionRequest(Long sessionId, LocalDateTime sessionDateTime) {
        return new UpdateSessionRequest(sessionId, "New Title", "New Description",
                sessionDateTime, "New Place", "도로 명 주소",
                Location.location(0.0, 0.0),
                null, false, false, ItIssue.IT_ON, Networking.NW_ON, CSEducation.CS_ON, DevTalk.DEVTALK_ON);
    }

    private AttendanceDeadLineDto attendanceDeadLineDto(LocalDateTime attendanceDeadLine, LocalDateTime lateDeadLine) {
        return new AttendanceDeadLineDto(attendanceDeadLine, lateDeadLine);
    }

    private Session mockSession(Long sessionId, LocalDateTime sessionDateTime) {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(sessionId);
        when(session.getSessionDateTime()).thenReturn(sessionDateTime);
        return session;
    }

    private Attendance mockAttendance() {
        return mock(Attendance.class);
    }
}