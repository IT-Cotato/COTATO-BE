package org.cotato.csquiz.common.sse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class SseSender {

    private static final String SUBSCRIBE_ATTENDANCE = "attendance";
    private static final String ATTENDANCE_STATUS = "AttendanceStatus";
    private static final String CONNECTED = "connected";

    private final AttendanceRepository attendanceRepository;

    public void sendAttendanceConnected(SseEmitter sseEmitter) throws IOException {
        sseEmitter.send(SseEmitter.event()
                .name(SUBSCRIBE_ATTENDANCE)
                .data(CONNECTED)
                .build());
    }

    public void sendInitialAttendanceStatus(SseEmitter sseEmitter) throws IOException {
        Optional<Attendance> maybeAttendance = attendanceRepository.findByAttendanceDeadLineDate(
                LocalDateTime.now());

        if (maybeAttendance.isEmpty()) {
            sseEmitter.send(SseEmitter.event()
                    .name(ATTENDANCE_STATUS)
                    .data(AttendanceOpenStatus.CLOSED)
                    .build());
            return;
        }

        sseEmitter.send(SseEmitter.event()
                .name(ATTENDANCE_STATUS)
                .data(AttendanceUtil.getAttendanceOpenStatus(maybeAttendance.get(), LocalDateTime.now()))
                .build());
    }
}
