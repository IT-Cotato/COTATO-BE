package org.cotato.csquiz.domain.attendance.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordCreationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestAttendanceService {

    private final Map<AttendanceRecordCreationType, AttendClient> clients;

    @Autowired
    public RequestAttendanceService(List<AttendClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(AttendClient::attendanceType, Function.identity())
        );
    }

    public AttendResponse attend(AttendanceParams params, LocalDateTime sessionStartTime, Long memberId, Attendance attendance) {
        AttendClient attendClient = clients.get(params.attendanceType());
        return attendClient.request(params, sessionStartTime, memberId, attendance);
    }
}
