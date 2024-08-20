package org.cotato.csquiz.common.sse;

import java.time.LocalDateTime;
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
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class SseSender {

    private static final String ATTENDANCE_STATUS = "AttendanceStatus";

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

        send(sseEmitter, SseEmitter.event()
                .name(ATTENDANCE_STATUS)
                .data(AttendanceStatusInfo.builder()
                        .attendanceId(maybeAttendance.get().getId())
                        .openStatus(AttendanceUtil.getAttendanceOpenStatus(maybeAttendance.get(), LocalDateTime.now()))
                        .build())
                .build());
    }

    private void send(SseEmitter sseEmitter, Set<DataWithMediaType> data) {
        try {
            sseEmitter.send(data);
        } catch (Exception e) {
            throw new AppException(ErrorCode.SSE_SEND_FAIL);
        }
    }
}
