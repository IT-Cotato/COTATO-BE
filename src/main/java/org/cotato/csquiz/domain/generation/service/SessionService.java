package org.cotato.csquiz.domain.generation.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.session.dto.AddSessionResponse;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.SessionWithAttendanceResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.event.CotatoEventPublisher;
import org.cotato.csquiz.common.event.EventType;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceRecordReader;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.entity.SessionImage;
import org.cotato.csquiz.domain.generation.enums.SessionType;
import org.cotato.csquiz.domain.generation.event.AttendanceEvent;
import org.cotato.csquiz.domain.generation.event.AttendanceEventDto;
import org.cotato.csquiz.domain.generation.event.SessionImageEvent;
import org.cotato.csquiz.domain.generation.event.SessionImageEventDto;
import org.cotato.csquiz.domain.generation.repository.AttendanceNotificationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionImageRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.cotato.csquiz.domain.generation.service.dto.SessionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final GenerationReader generationReader;
    private final SessionImageRepository sessionImageRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordReader attendanceRecordReader;
    private final SessionReader sessionReader;
    private final AttendanceReader attendanceReader;
    private final CotatoEventPublisher cotatoEventPublisher;
    private final AttendanceNotificationRepository attendanceNotificationRepository;

    @Transactional
    public AddSessionResponse addSession(final Long generationId,
                                         final List<MultipartFile> images,
                                         final SessionDto sessionDto,
                                         final LocalDateTime attendanceDeadLine,
                                         final LocalDateTime lateDeadLine,
                                         final Location location) {
        Generation generation = generationReader.findById(generationId);

        int sessionNumber = calculateLastSessionNumber(generation);
        Session session = Session.builder()
                .generation(generation)
                .number(sessionNumber + 1)
                .title(sessionDto.title())
                .description(sessionDto.description())
                .placeName(sessionDto.placeName())
                .sessionDateTime(sessionDto.sessionDateTime())
                .roadNameAddress(sessionDto.roadNameAddress())
                .sessionContents(sessionDto.sessionContents())
                .sessionType(sessionDto.type())
                .build();

        sessionRepository.save(session);

        SessionImageEventDto sessionImageEventDto = SessionImageEventDto.builder().images(images).session(session)
                .build();
        SessionImageEvent sessionImageEvent = SessionImageEvent.builder().type(EventType.SESSION_IMAGE_UPDATE)
                .data(sessionImageEventDto).build();
        cotatoEventPublisher.publishEvent(sessionImageEvent);

        AttendanceEventDto attendanceEventDto = AttendanceEventDto.builder().session(session).location(location)
                .attendanceDeadLine(attendanceDeadLine).lateDeadLine(lateDeadLine).build();
        AttendanceEvent attendanceEvent = AttendanceEvent.builder().type(EventType.ATTENDANCE_CREATE)
                .data(attendanceEventDto)
                .build();
        cotatoEventPublisher.publishEvent(attendanceEvent);

        return AddSessionResponse.from(session);
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
            attendanceNotificationRepository.deleteAllByAttendance(attendance);
            attendanceRepository.delete(attendance);
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
        Generation generation = generationReader.findById(generationId);

        List<Session> sessions = sessionRepository.findAllByGeneration(generation);

        Map<Long, List<SessionImage>> imagesGroupBySession = sessionImageRepository.findAllBySessionIn(sessions)
                .stream()
                .sorted(Comparator.comparing(SessionImage::getOrder))
                .collect(Collectors.groupingBy(sessionImage -> sessionImage.getSession().getId()));

        return sessions.stream()
                .map(session -> SessionListResponse.of(session,
                        imagesGroupBySession.getOrDefault(session.getId(), List.of())))
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
