package org.cotato.csquiz.domain.attendance.service;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.api.attendance.dto.OfflineAttendanceRequest;
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
public class OfflineAttendClient implements AttendClient {

    private static final Double ACCURACY_STANDARD = 0.1;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    public AttendanceType attendanceType() {
        return AttendanceType.OFFLINE;
    }

    @Override
    public AttendResponse request(AttendanceParams params, Long memberId, Attendance attendance) {
        OfflineAttendanceRequest request = (OfflineAttendanceRequest) params;

        AttendanceStatus attendanceStatus = AttendanceUtil.calculateAttendanceStatus(attendance, params.requestTime());

        Double accuracy = attendance.getLocation().calculateAccuracy(request.getLocation());
        validateAccuracy(accuracy);

        AttendanceRecord attendanceRecord = attendanceRecordRepository.findByMemberIdAndAttendanceId(memberId,
                        request.getAttendanceId())
                .orElseGet(() -> AttendanceRecord.offlineRecord(attendance, memberId, accuracy, attendanceStatus, request.getRequestTime()));

        attendanceRecord.updateAttendanceType(request.attendanceType());
        attendanceRecord.updateLocationAccuracy(accuracy);

        attendanceRecordRepository.save(attendanceRecord);

        return AttendResponse.from(attendanceStatus);
    }

    private void validateAccuracy(Double accuracy) {
        if (accuracy >= ACCURACY_STANDARD) {
            throw new AppException(ErrorCode.OFFLINE_ATTEND_FAIL);
        }
    }
}
