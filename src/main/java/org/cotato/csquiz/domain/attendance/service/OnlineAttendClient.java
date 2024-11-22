package org.cotato.csquiz.domain.attendance.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnlineAttendClient implements AttendClient {

    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    public AttendanceType attendanceType() {
        return AttendanceType.ONLINE;
    }

    @Override
    public AttendResponse request(AttendanceParams params, Session session, Long memberId, Attendance attendance) {
        AttendanceResult attendanceResult = AttendanceUtil.calculateAttendanceStatus(session, attendance, params.requestTime(), attendanceType());

        attendanceRecordRepository.save(AttendanceRecord.onLineRecord(attendance, memberId, attendanceResult, params.requestTime()));

        return AttendResponse.from(attendanceResult);
    }
}