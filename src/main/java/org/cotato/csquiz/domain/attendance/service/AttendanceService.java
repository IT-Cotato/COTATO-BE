package org.cotato.csquiz.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendanceResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceWithSessionResponse;
import org.cotato.csquiz.api.attendance.dto.AttendancesResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceTimeResponse;
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
        Session session = sessionReader.findById(attendanceId);
        return AttendanceResponse.of(attendance, session);
    }

    @Transactional(readOnly = true)
    public AttendancesResponse findAttendancesByGenerationId(final Long generationId) {
        Generation findGeneration = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        List<Session> sessions = sessionRepository.findAllByGenerationId(generationId);

        Map<Long, Session> sessionMap = sessions.stream()
                .collect(Collectors.toMap(Session::getId, Function.identity()));

        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        LocalDateTime currentTime = LocalDateTime.now();

        List<AttendanceWithSessionResponse> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds).stream()
                .map(at -> AttendanceWithSessionResponse.builder()
                        .attendanceId(at.getId())
                        .sessionId(at.getSessionId())
                        .sessionTitle(sessionMap.get(at.getSessionId()).getTitle())
                        .sessionDateTime(sessionMap.get(at.getSessionId()).getSessionDateTime())
                        .openStatus(AttendanceUtil.getAttendanceOpenStatus(sessionMap.get(at.getSessionId()).getSessionDateTime(), at, currentTime))
                        .build())
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
}
