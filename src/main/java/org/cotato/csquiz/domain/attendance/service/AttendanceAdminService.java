package org.cotato.csquiz.domain.attendance.service;


import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
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
    private final SessionRepository sessionRepository;

    @Transactional
    public void addAttendance(Session session, Location location,
                              AttendanceDeadLineDto attendanceDeadLine) {

        Attendance attendance = Attendance.builder()
                .session(session)
                .location(location)
                .startTime(LocalDateTime.of(session.getSessionDate(), attendanceDeadLine.startTime()))
                .endTime(LocalDateTime.of(session.getSessionDate(), attendanceDeadLine.endTime()))
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

        LocalDate sessionDate = attendanceSession.getSessionDate();
        AttendanceDeadLineDto deadLine = request.attendanceDeadLine();
        LocalDateTime startLocalDateTime = LocalDateTime.of(sessionDate, deadLine.startTime());
        LocalDateTime endLocalDateTime = LocalDateTime.of(sessionDate, deadLine.endTime());

        attendance.updateDeadLine(startLocalDateTime, endLocalDateTime);
        attendance.updateLocation(request.location());
    }
}
