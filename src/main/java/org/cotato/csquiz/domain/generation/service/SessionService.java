package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.session.dto.AddSessionRequest;
import org.cotato.csquiz.api.session.dto.AddSessionResponse;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.SessionWithAttendanceResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.AttendanceService;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceRecordReader;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;
import org.cotato.csquiz.domain.generation.enums.SessionType;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionImageRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
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
    private final AttendanceService attendanceService;
    private final SessionImageService sessionImageService;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordReader attendanceRecordReader;
    private final SessionReader sessionReader;
    private final AttendanceReader attendanceReader;

    @Transactional
    public AddSessionResponse addSession(AddSessionRequest request) throws ImageException {
        Generation findGeneration = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        int sessionNumber = calculateLastSessionNumber(findGeneration);
        log.info("해당 기수에 추가된 마지막 세션 : {}", sessionNumber);

        SessionType sessionType = SessionType.getSessionType(request.isOffline(), request.isOnline());
        Session session = Session.builder()
                .number(sessionNumber + 1)
                .description(request.description())
                .generation(findGeneration)
                .title(request.title())
                .roadNameAddress(request.roadNameAddress())
                .placeName(request.placeName())
                .sessionType(sessionType)
                .sessionDateTime(request.sessionDateTime())
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

        if (sessionType.isCreateAttendance()) {
            if (isAttendanceDeadLineNotExist(request.attendanceDeadLine(), request.lateDeadLine())) {
                throw new AppException(ErrorCode.INVALID_ATTEND_DEADLINE);
            }
            attendanceService.createAttendance(session, Location.location(request.latitude(), request.longitude()),
                    request.attendanceDeadLine(), request.lateDeadLine());
        }

        return AddSessionResponse.from(savedSession);
    }

    private int calculateLastSessionNumber(Generation generation) {
        List<Session> allSession = sessionRepository.findAllByGenerationId(generation.getId());
        return allSession.stream().mapToInt(Session::getNumber).max()
                .orElse(-1);
    }

    @Transactional
    public void updateSession(UpdateSessionRequest request) {
        Session session = sessionReader.findByIdWithPessimisticXLock(request.sessionId());
        SessionType sessionType = SessionType.getSessionType(request.isOffline(), request.isOnline());
        SessionContents sessionContents = SessionContents.of(request.itIssue(), request.networking(),
                request.csEducation(), request.devTalk());

        if (sessionType.isCreateAttendance()) {
            AttendanceDeadLineDto deadLineDto = request.attendTime();
            if (deadLineDto == null || isAttendanceDeadLineNotExist(deadLineDto.attendanceDeadLine(),
                    deadLineDto.lateDeadLine())) {
                throw new AppException(ErrorCode.INVALID_ATTEND_DEADLINE);
            }
        }

        Optional<Attendance> maybeAttendance = attendanceReader.findBySessionIdWithPessimisticXLock(session.getId());
        if (maybeAttendance.isPresent() && attendanceRecordReader.isAttendanceRecordExist(maybeAttendance.get())) {
            validateAttendanceUpdatable(session, sessionType, request.sessionDateTime());
        }

        session.updateDescription(request.description());
        session.updateSessionTitle(request.title());
        session.updateSessionPlace(request.placeName());
        session.updateRoadNameAddress(request.roadNameAddress());
        session.updateSessionContents(sessionContents);
        session.updateSessionDateTime(request.sessionDateTime());
        session.updateSessionType(sessionType);
        sessionRepository.save(session);

        if (!sessionType.isCreateAttendance() && maybeAttendance.isPresent()) {
            Attendance attendance = maybeAttendance.get();
            attendanceRepository.deleteById(attendance.getId());
            return;
        }

        // Todo https://www.notion.so/youthhing/ApplicationEventPublisher-15887d592b6e803eb7c7c1ce2da22b8c?pvs=4
        AttendanceUtil.validateAttendanceTime(request.sessionDateTime(), request.attendTime().attendanceDeadLine(),
                request.attendTime().lateDeadLine());
        Attendance attendance = maybeAttendance.orElseGet(() ->
                Attendance.builder()
                        .session(session)
                        .attendanceDeadLine(request.attendTime().attendanceDeadLine())
                        .lateDeadLine(request.attendTime().lateDeadLine())
                        .build());
        attendance.updateDeadLine(request.attendTime().attendanceDeadLine(), request.attendTime().lateDeadLine());
        if (sessionType.hasOffline()) {
            attendance.updateLocation(request.location());
        }
        attendanceRepository.save(attendance);
    }

    private boolean isAttendanceDeadLineNotExist(LocalDateTime attendanceDeadLine, LocalDateTime lateDeadLine) {
        return attendanceDeadLine == null || lateDeadLine == null;
    }

    private void validateAttendanceUpdatable(Session session, SessionType sessionType, LocalDateTime newSessionDate) {
        if (!(session.getSessionDateTime().isEqual(newSessionDate) && sessionType.isCreateAttendance())) {
            throw new AppException(ErrorCode.ATTENDANCE_RECORD_EXIST);
        }
    }

    public List<SessionListResponse> findSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        List<Session> sessions = sessionRepository.findAllByGeneration(generation);

        Map<Session, List<SessionImage>> imagesGroupBySession = sessionImageRepository.findAllBySessionIn(sessions)
                .stream()
                .sorted(Comparator.comparing(SessionImage::getOrder))
                .collect(Collectors.groupingBy(SessionImage::getSession));

        return sessions.stream()
                .map(session -> SessionListResponse.of(session, imagesGroupBySession.getOrDefault(session, List.of())))
                .toList();
    }

    public SessionWithAttendanceResponse findSession(Long sessionId) {
        Session session = sessionReader.findById(sessionId);
        List<SessionImage> sessionImages = sessionImageRepository.findAllBySession(session);
        Optional<Attendance> maybeAttendance = attendanceRepository.findBySessionId(sessionId);
        return maybeAttendance.map(attendance -> SessionWithAttendanceResponse.of(session, sessionImages, attendance))
                .orElseGet(() -> SessionWithAttendanceResponse.of(session, sessionImages));
    }
}
