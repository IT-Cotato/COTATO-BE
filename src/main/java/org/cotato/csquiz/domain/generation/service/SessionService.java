package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.session.dto.AddSessionRequest;
import org.cotato.csquiz.api.session.dto.AddSessionResponse;
import org.cotato.csquiz.api.session.dto.CsEducationOnSessionNumberResponse;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionNumberRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.schedule.SchedulerService;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.AttendanceAdminService;
import org.cotato.csquiz.domain.attendance.service.AttendanceRecordService;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionImageRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository;
    private final GenerationRepository generationRepository;
    private final SessionImageRepository sessionImageRepository;
    private final AttendanceAdminService attendanceAdminService;
    private final EducationService educationService;
    private final SessionImageService sessionImageService;
    private final SchedulerService schedulerService;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordService attendanceRecordService;

    @Transactional
    public AddSessionResponse addSession(AddSessionRequest request) throws ImageException {
        Generation findGeneration = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        int sessionNumber = calculateLastSessionNumber(findGeneration);
        log.info("해당 기수에 추가된 마지막 세션 : {}", sessionNumber);

        Session session = Session.builder()
                .number(sessionNumber + 1)
                .description(request.description())
                .generation(findGeneration)
                .title(request.title())
                .placeName(request.placeName())
                .sessionDate(request.sessionDate())
                .sessionContents(SessionContents.builder()
                        .csEducation(request.csEducation())
                        .devTalk(request.devTalk())
                        .itIssue(request.itIssue())
                        .networking(request.networking())
                        .build())
                .build();
        Session savedSession = sessionRepository.save(session);
        log.info("세션 생성 완료");

        if (request.images() != null && !request.images().isEmpty()) {
            sessionImageService.addSessionImages(request.images(), savedSession);
        }

        Location location = Location.builder()
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();

        AttendanceDeadLineDto attendanceDeadLine = AttendanceDeadLineDto.builder()
                .attendanceDeadLine(request.attendanceDeadLine())
                .lateDeadLine(request.lateDeadLine())
                .build();

        attendanceAdminService.addAttendance(session, location, attendanceDeadLine);
        schedulerService.scheduleSessionNotification(savedSession.getSessionDate());

        return AddSessionResponse.from(savedSession);
    }

    private int calculateLastSessionNumber(Generation generation) {
        List<Session> allSession = sessionRepository.findAllByGenerationId(generation.getId());
        return allSession.stream().mapToInt(Session::getNumber).max()
                .orElse(-1);
    }

    @Transactional
    public void updateSessionNumber(UpdateSessionNumberRequest request) {
        Session session = findSessionById(request.sessionId());
        session.changeSessionNumber(session.getNumber());
    }

    @Transactional
    public void updateSession(UpdateSessionRequest request) {
        Session session = findSessionById(request.sessionId());

        session.updateDescription(request.description());
        session.updateSessionTitle(request.title());
        session.updateSessionPlace(request.placeName());

        session.updateSessionContents(SessionContents.builder()
                .csEducation(request.csEducation())
                .devTalk(request.devTalk())
                .itIssue(request.itIssue())
                .networking(request.networking())
                .build());

        updateSessionDate(session, request.sessionDate(), request.attendTime());
        sessionRepository.save(session);
    }

    public void updateSessionDate(Session session, LocalDate newDate, AttendanceDeadLineDto newDeadline) {
        Attendance findAttendance = attendanceRepository.findBySessionId(session.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 세션의 출석이 존재하지 않습니다"));


        // 날짜가 바뀌지 않았고, 출결 시간이 모두 동일한 경우
        if (newDate.equals(session.getSessionDate()) &&
                findAttendance.getAttendanceDeadLine().toLocalTime().equals(newDeadline.attendanceDeadLine()) &&
                findAttendance.getLateDeadLine().toLocalTime().equals(newDeadline.lateDeadLine())) {
            return;
        }
        session.updateSessionDate(newDate);

        LocalDateTime newAttendanceDeadline = LocalDateTime.of(newDate, newDeadline.attendanceDeadLine());
        LocalDateTime newLateDeadline = LocalDateTime.of(newDate, newDeadline.lateDeadLine());
        findAttendance.updateDeadLine(newAttendanceDeadline, newLateDeadline);

        attendanceRecordService.updateAttendanceStatus(findAttendance);
    }

    public List<SessionListResponse> findSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        List<Session> sessions = sessionRepository.findAllByGeneration(generation);

        Map<Session, List<SessionImage>> imagesGroupBySession = sessionImageRepository.findAllBySessionIn(sessions)
                .stream()
                .collect(Collectors.groupingBy(SessionImage::getSession));

        return sessions.stream()
                .map(session -> SessionListResponse.of(session, imagesGroupBySession.getOrDefault(session, List.of())))
                .toList();
    }

    public Session findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
    }

    public List<CsEducationOnSessionNumberResponse> findAllNotLinkedCsOnSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));
        List<Session> sessions = sessionRepository.findAllByGenerationAndSessionContentsCsEducation(generation,
                CSEducation.CS_ON);

        List<Long> educationLinkedSessionIds = educationService.findAllEducationByGenerationId(generationId).stream()
                .map(Education::getSessionId)
                .toList();

        return sessions.stream()
                .filter(session -> !educationLinkedSessionIds.contains(session.getId()))
                .map(CsEducationOnSessionNumberResponse::from)
                .toList();
    }
}
