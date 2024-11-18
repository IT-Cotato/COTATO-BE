package org.cotato.csquiz.domain.attendance.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordCreationType;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnlineAttendClient implements AttendClient {

    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    public AttendanceRecordCreationType attendanceType() {
        return AttendanceRecordCreationType.ONLINE;
    }

    @Override
    public AttendResponse request(AttendanceParams params, LocalDateTime sessionStartTime, Long memberId, Attendance attendance) {
        AttendanceResult attendanceResult = AttendanceUtil.calculateAttendanceStatus(sessionStartTime, attendance, params.requestTime());

        attendanceRecordRepository.save(AttendanceRecord.onLineRecord(attendance, memberId, attendanceResult, params.requestTime()));

        return AttendResponse.from(attendanceResult);
    }
}