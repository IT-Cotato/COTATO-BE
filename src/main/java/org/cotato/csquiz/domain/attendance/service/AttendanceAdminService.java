package org.cotato.csquiz.domain.attendance.service;


import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.api.session.dto.AddSessionRequest.AttendanceDeadLine;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
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
    public void addAttendance(Session session, LocalDate localDate, Location location,
                              AttendanceDeadLine attendanceDeadLine) {

        if (checkAttendanceTimeValid(attendanceDeadLine.startTime(), attendanceDeadLine.endTime())) {
            throw new AppException(ErrorCode.SESSION_DEADLINE_INVALID);
        }

        Attendance attendance = Attendance.builder()
                .session(session)
                .location(location)
                .startTime(LocalDateTime.of(localDate, attendanceDeadLine.startTime()))
                .endTime(LocalDateTime.of(localDate, attendanceDeadLine.endTime()))
                .build();

        attendanceRepository.save(attendance);
    }


    @Transactional
    public void updateAttendance(UpdateAttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석 정보가 존재하지 않습니다"));
        Session attendanceSession = sessionRepository.findById(attendance.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("출석과 연결된 세션을 찾을 수 없습니다"));

        if (attendanceSession.getSessionDate() == null) {
            throw new AppException(ErrorCode.SESSION_DATE_NOT_FOUND);
        }

        if (checkAttendanceTimeValid(request.attendanceDeadLine().startTime(), request.attendanceDeadLine().endTime())) {
            throw new AppException(ErrorCode.SESSION_DEADLINE_INVALID);
        }

        attendance.updateDeadLine(attendanceSession.getSessionDate(), request.attendanceDeadLine());
        attendance.updateLocation(request.location());
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

    public List<AttendanceRecordResponse> findAttendanceRecordsByAttendance(Long attendanceId){
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다"));

        return attendanceRecordService.generateAttendanceResponses(List.of(attendance));
    }

    private boolean checkAttendanceTimeValid(LocalTime startTime, LocalTime endTime) {
        return endTime == null || startTime == null;
    }
}
