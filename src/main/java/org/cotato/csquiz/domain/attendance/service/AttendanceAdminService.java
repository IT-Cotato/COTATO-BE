package org.cotato.csquiz.domain.attendance.service;


import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttendanceAdminService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordService attendanceRecordService;
    private final SessionRepository sessionRepository;

    @Transactional
    public void addAttendance(Session session, Location location, LocalTime attendanceDeadline, LocalTime lateDeadline) {
        AttendanceUtil.validateAttendanceTime(attendanceDeadline, lateDeadline);

        Attendance attendance = Attendance.builder()
                .session(session)
                .location(location)
                .attendanceDeadLine(LocalDateTime.of(session.getSessionDateTime().toLocalDate(), attendanceDeadline))
                .lateDeadLine(LocalDateTime.of(session.getSessionDateTime().toLocalDate(), lateDeadline))
                .build();

        attendanceRepository.save(attendance);
    }

    @Transactional
    public void updateAttendanceByAttendanceId(UpdateAttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석 정보가 존재하지 않습니다"));
        Session attendanceSession = sessionRepository.findById(attendance.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("출석과 연결된 세션을 찾을 수 없습니다"));

        updateAttendance(attendanceSession, attendance, request.attendTime(), request.location());
    }

    @Transactional
    public void updateAttendance(Session attendanceSession, Attendance attendance,
                                 AttendanceDeadLineDto attendanceDeadLine, Location location) {
        AttendanceUtil.validateAttendanceTime(attendanceDeadLine.attendanceDeadLine(),
                attendanceDeadLine.lateDeadLine());

        // 세션 날짜가 존재하지 않는 경우 예외 발생
        if (attendanceSession.getSessionDateTime() == null) {
            throw new AppException(ErrorCode.SESSION_DATE_NOT_FOUND);
        }

        attendance.updateDeadLine(LocalDateTime.of(attendanceSession.getSessionDateTime().toLocalDate(), attendanceDeadLine.attendanceDeadLine()),
                LocalDateTime.of(attendanceSession.getSessionDateTime().toLocalDate(), attendanceDeadLine.lateDeadLine()));
        attendance.updateLocation(location);

        attendanceRecordService.updateAttendanceStatus(attendanceSession.getSessionDateTime(), attendance);
    }

    public List<AttendanceRecordResponse> findAttendanceRecords(Long generationId, Integer month) {
        List<Session> sessions = sessionRepository.findAllByGenerationId(generationId);
        if (month != null) {
            sessions = sessions.stream()
                    .filter(session -> session.getSessionDate().getMonthValue() == month)
                    .toList();
        }
        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        List<Attendance> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds);

        return attendanceRecordService.generateAttendanceResponses(attendances);
    }

    public List<AttendanceRecordResponse> findAttendanceRecordsByAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다"));

        return attendanceRecordService.generateAttendanceResponses(List.of(attendance));
    }
}
