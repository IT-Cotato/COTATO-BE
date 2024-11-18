package org.cotato.csquiz.domain.attendance.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.api.attendance.dto.OfflineAttendanceRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfflineAttendClient implements AttendClient {

    @Value("${location.distance}")
    private Double standardDistance;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    public AttendanceType attendanceType() {
        return AttendanceType.OFFLINE;
    }

    @Override
    public AttendResponse request(AttendanceParams params, LocalDateTime sessionStartTime, Long memberId, Attendance attendance) {
        OfflineAttendanceRequest request = (OfflineAttendanceRequest) params;

        AttendanceResult attendanceResult = AttendanceUtil.calculateAttendanceStatus(sessionStartTime, attendance, params.requestTime());

        log.info("[출결 위치 로그: 위도 {}, 경도 {}]", request.getLocation().getLatitude(), request.getLocation().getLongitude());
        Double accuracy = attendance.getLocation().calculateAccuracy(request.getLocation());
        validateAccuracy(accuracy);

        AttendanceRecord attendanceRecord = attendanceRecordRepository.findByMemberIdAndAttendanceId(memberId,
                        request.getAttendanceId())
                .orElseGet(() -> AttendanceRecord.offlineRecord(attendance, memberId, accuracy, attendanceResult,
                        request.getRequestTime()));

        attendanceRecord.updateAttendanceType(request.attendanceType());
        attendanceRecord.updateLocationAccuracy(accuracy);

        attendanceRecordRepository.save(attendanceRecord);

        return AttendResponse.from(attendanceResult);
    }

    private void validateAccuracy(Double accuracy) {
        log.info("[위치 정확도] : {}", accuracy);
        if (accuracy >= standardDistance) {
            throw new AppException(ErrorCode.OFFLINE_ATTEND_FAIL);
        }
    }
}
