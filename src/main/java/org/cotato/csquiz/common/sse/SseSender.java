package org.cotato.csquiz.common.sse;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.event.dto.AttendanceStatusInfo;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.generation.entity.AttendanceNotification;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.AttendanceNotificationRepository;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseSender {

    private static final String ATTENDANCE_STATUS = "AttendanceStatus";
    private final SseAttendanceRepository sseAttendanceRepository;
    private final SessionReader sessionReader;
    private final AttendanceNotificationRepository sessionNotificationRepository;

    public void sendInitialAttendanceStatus(final SseEmitter sseEmitter, final Long attendanceId, final AttendanceOpenStatus openStatus) {
        Set<DataWithMediaType> event = SseEmitter.event()
                .name(ATTENDANCE_STATUS)
                .data(AttendanceStatusInfo.builder()
                        .attendanceId(attendanceId)
                        .openStatus(openStatus)
                        .build())
                .build();
        send(sseEmitter, event);
    }

    @Transactional
    public void sendAttendanceStartNotification(final AttendanceNotification attendanceNotification) {
        Attendance attendance = attendanceNotification.getAttendance();
        Session session = sessionReader.findById(attendance.getSessionId());

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
        log.info("[send attendance notification: session id <{}>, time <{}>]", attendance.getSessionId(), session.getSessionDateTime());
        attendanceNotification.done();
        sessionNotificationRepository.save(attendanceNotification);
    }

    private void send(SseEmitter sseEmitter, Set<DataWithMediaType> data) {
        try {
            sseEmitter.send(data);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SSE_SEND_FAIL);
        }
    }

    public void sendEvents(Attendance attendance) {
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
}
