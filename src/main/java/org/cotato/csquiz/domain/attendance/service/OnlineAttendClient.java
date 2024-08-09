package org.cotato.csquiz.domain.attendance.service;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
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
    public AttendResponse request(AttendanceParams params, Long memberId, Attendance attendance) {
        AttendanceStatus attendanceStatus = AttendanceUtil.calculateAttendanceStatus(attendance, params.requestTime());

        attendanceRecordRepository.save(AttendanceRecord.onLineRecord(attendance, memberId, attendanceStatus, params.requestTime()));

        return AttendResponse.from(attendanceStatus);
    }
}