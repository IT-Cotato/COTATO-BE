package org.cotato.csquiz.domain.attendance.service;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnlineAttendClient implements AttendClient{

    private final AttendanceRecordRepository attendanceRecordRepository;
    @Override
    public AttendanceType attendanceType() {
        return AttendanceType.ONLINE;
    }

    @Override
    public AttendResponse request(AttendanceParams params, Long memberId, Attendance attendance) {
        AttendanceStatus attendanceStatus = AttendanceUtil.calculateAttendanceStatus(attendance, params.requestTime());

        AttendanceRecord attendanceRecord = attendanceRecordRepository.findAttendanceByIdAndMemberId(params.attendanceId(), memberId)
                .orElseGet(() -> AttendanceRecord.onLineRecord(attendance, memberId, attendanceStatus));

        validateAlreadyAttendOffline(attendanceRecord);

        attendanceRecord.updateAttendanceType(params.attendanceType());
        attendanceRecord.updateAttendanceStatus(attendanceStatus);

        attendanceRecordRepository.save(AttendanceRecord.onLineRecord(attendance, memberId, attendanceStatus));

        return AttendResponse.from(attendanceStatus);
    }

    private void validateAlreadyAttendOffline(AttendanceRecord attendanceRecord) {
        if (attendanceRecord.getAttendanceType() == AttendanceType.OFFLINE) {
            throw new AppException(ErrorCode.ALREADY_ATTEND);
        }
    }
}
