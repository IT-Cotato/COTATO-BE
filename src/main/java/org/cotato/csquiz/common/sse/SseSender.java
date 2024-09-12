package org.cotato.csquiz.common.sse;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.event.dto.AttendanceStatusInfo;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class SseSender {

    private static final String ATTENDANCE_STATUS = "AttendanceStatus";
    private final SseAttendanceRepository sseAttendanceRepository;
    private final SessionRepository sessionRepository;
    private final AttendanceRepository attendanceRepository;

    public void sendInitialAttendanceStatus(SseEmitter sseEmitter) {
        Optional<Attendance> maybeAttendance = attendanceRepository.findByAttendanceDeadLineDate(LocalDateTime.now());

        if (maybeAttendance.isEmpty()) {
            send(sseEmitter, SseEmitter.event()
                    .name(ATTENDANCE_STATUS)
                    .data(AttendanceStatusInfo.builder()
                            .openStatus(AttendanceOpenStatus.CLOSED)
                            .build())
                    .build());
            return;
        }

        Attendance attendance = maybeAttendance.get();
        Session session = sessionRepository.findById(attendance.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석에 대한 세션이 존재하지 않습니다."));

        send(sseEmitter, SseEmitter.event()
                .name(ATTENDANCE_STATUS)
                .data(AttendanceStatusInfo.builder()
                        .attendanceId(maybeAttendance.get().getId())
                        .openStatus(AttendanceUtil.getAttendanceOpenStatus(session.getSessionDateTime(), attendance,
                                LocalDateTime.now()))
                        .build())
                .build());
    }

    // sessionDate 6시 50분에 출결을 구독 중인 부원들에게 출결 입력 시작 알림을 전송한다.
    public void sendNotification(LocalDateTime notificationDate) {
        Attendance attendance = attendanceRepository.findByAttendanceDeadLineDate(notificationDate)
                .orElseThrow(() -> new EntityNotFoundException("해당 날짜에 진행하는 출석이 없습니다."));

        Set<DataWithMediaType> data = SseEmitter.event()
                .name(ATTENDANCE_STATUS)
                .data(AttendanceStatusInfo.builder()
                        .attendanceId(attendance.getId())
                        .openStatus(AttendanceOpenStatus.OPEN)
                        .build())
                .build();

        List<SseEmitter> sseEmitters = sseAttendanceRepository.findAll();
        for (SseEmitter sseEmitter : sseEmitters) {
            send(sseEmitter, data);
        }
    }

    private void send(SseEmitter sseEmitter, Set<DataWithMediaType> data) {
        try {
            sseEmitter.send(data);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SSE_SEND_FAIL);
        }
    }
}
