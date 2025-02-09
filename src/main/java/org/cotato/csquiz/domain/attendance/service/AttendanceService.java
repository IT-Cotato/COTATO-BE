package org.cotato.csquiz.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendanceResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceWithSessionResponse;
import org.cotato.csquiz.api.attendance.dto.AttendancesResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceTimeResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceReader attendanceReader;
    private final SessionReader sessionReader;
    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final GenerationRepository generationRepository;

    public AttendanceResponse getAttendance(final Long attendanceId) {
        Attendance attendance = attendanceReader.findById(attendanceId);
        Session session = sessionReader.findById(attendance.getSessionId());
        return AttendanceResponse.of(attendance, session);
    }

    @Transactional
    public void createAttendance(Session session, Location location, LocalDateTime attendanceDeadline, LocalDateTime lateDeadline) {
        AttendanceUtil.validateAttendanceTime(session.getSessionDateTime(), attendanceDeadline, lateDeadline);
        if (session.hasOfflineSession()) {
            checkLocation(location);
        }
        Attendance attendance = Attendance.builder()
                .session(session)
                .location(location)
                .attendanceDeadLine(attendanceDeadline)
                .lateDeadLine(lateDeadline)
                .build();

        attendanceRepository.save(attendance);
    }

    private void checkLocation(Location location) {
        if (location == null) {
            throw new AppException(ErrorCode.INVALID_LOCATION);
        }
    }

    @Transactional(readOnly = true)
    public AttendancesResponse findAttendancesByGenerationId(final Long generationId) {
        Generation findGeneration = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        List<Session> sessions = sessionRepository.findAllByGenerationId(generationId);

        Map<Long, Session> sessionById = sessions.stream()
                .collect(Collectors.toMap(Session::getId, Function.identity()));

        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        List<AttendanceWithSessionResponse> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds).stream()
                .map(at -> {
                    final Session session = Optional.ofNullable(sessionById.get(at.getSessionId()))
                                .orElseThrow(() -> new EntityNotFoundException("출석에 연결된 세션을 찾을 수 없습니다."));

                    return AttendanceWithSessionResponse.builder()
                        .attendanceId(at.getId())
                        .sessionType(session.getSessionType())
                        .sessionId(at.getSessionId())
                        .sessionTitle(session.getTitle())
                        .sessionDateTime(session.getSessionDateTime())
                        .openStatus(AttendanceUtil.getAttendanceOpenStatus(session.getSessionDateTime(), at, LocalDateTime.now()))
                        .build();
                })
                .toList();

        return AttendancesResponse.builder()
                .generationId(generationId)
                .generationNumber(findGeneration.getId())
                .attendances(attendances)
                .build();
    }

    @Transactional(readOnly = true)
    public AttendanceTimeResponse getAttendanceDetailInfo(final Long sessionId) {
        Attendance attendance = attendanceRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석을 찾을 수 없습니다"));

        return AttendanceTimeResponse.from(attendance);
    }

    @Transactional
    public void updateAttendance(final Long attendanceId, final Location location, final LocalDateTime attendDeadline, final LocalDateTime lateDeadline) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석 정보가 존재하지 않습니다"));
        Session attendanceSession = sessionReader.findById(attendance.getSessionId());

        AttendanceUtil.validateAttendanceTime(attendanceSession.getSessionDateTime(), attendDeadline, lateDeadline);

        if (attendanceSession.getSessionDateTime() == null) {
            throw new AppException(ErrorCode.SESSION_DATE_NOT_FOUND);
        }

        attendance.updateDeadLine(attendDeadline, lateDeadline);
        attendance.updateLocation(location);
    }
}
