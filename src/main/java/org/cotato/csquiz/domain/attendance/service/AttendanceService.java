package org.cotato.csquiz.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendanceResponse;
import org.cotato.csquiz.api.attendance.dto.AttendancesResponse;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SessionRepository sessionRepository;
    private final GenerationRepository generationRepository;

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

        List<AttendanceResponse> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds).stream()
                .map(at -> AttendanceResponse.builder()
                        .attendanceId(at.getId())
                        .sessionTitle(sessionMap.get(at.getSessionId()).getTitle())
                        .sessionDate(at.getAttendanceDeadLine().toLocalDate())
                        .openStatus(AttendanceUtil.getAttendanceOpenStatus(at, currentTime))
                        .build())
                .toList();

        return AttendancesResponse.builder()
                .generationId(generationId)
                .generationNumber(findGeneration.getId())
                .attendances(attendances)
                .build();
    }
}
