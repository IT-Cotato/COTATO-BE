package org.cotato.csquiz.common.sse;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.auth.component.GenerationMemberAuthValidator;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.enums.SessionType;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private static final Long DEFAULT_TIMEOUT = 60 * 1000 * 60L;

    private final GenerationReader generationReader;
    private final GenerationMemberAuthValidator generationMemberAuthValidator;
    private final AttendanceRepository attendanceRepository;
    private final SseAttendanceRepository sseAttendanceRepository;
    private final SseSender sseSender;
    private final SessionReader sessionReader;
    private final AttendanceReader attendanceReader;

    public SseEmitter subscribeAttendance(final Member member) {
        LocalDateTime now = LocalDateTime.now();
        Generation currentGeneration = generationReader.findByDate(now.toLocalDate());
        generationMemberAuthValidator.checkGenerationPermission(member, currentGeneration);

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        setBaseEmitterConfiguration(member.getId(), sseEmitter);
        sseAttendanceRepository.save(member.getId(), sseEmitter);

        Optional<Session> maybeSession = sessionReader.getByDate(now.toLocalDate());
        if (maybeSession.isEmpty()) {
            sseSender.sendInitialAttendanceStatus(sseEmitter, null, AttendanceOpenStatus.CLOSED);
            return sseEmitter;
        }

        Session session = maybeSession.get();
        if (session.getSessionType() == SessionType.NO_ATTEND) {
            sseSender.sendInitialAttendanceStatus(sseEmitter, null, AttendanceOpenStatus.CLOSED);
            return sseEmitter;
        }

        Attendance attendance = attendanceReader.findBySession(session);
        sseSender.sendInitialAttendanceStatus(sseEmitter, attendance.getId(), AttendanceUtil.getAttendanceOpenStatus(session.getSessionDateTime(), attendance, now));

        return sseEmitter;
    }

    private void setBaseEmitterConfiguration(Long memberId, SseEmitter sseEmitter) {
        sseEmitter.onCompletion(() -> {
            log.info("---- [memberId]: {} on completion callback ----", memberId);
            sseAttendanceRepository.deleteById(memberId);
        });

        sseEmitter.onTimeout(() -> {
            log.info("---- [memberId]: {} on timeout callback ----", memberId);
            sseEmitter.complete();
        });
    }

    public void sendEvent(final Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석을 찾을 수 없습니다."));
        sseSender.sendEvents(attendance);
    }
}
