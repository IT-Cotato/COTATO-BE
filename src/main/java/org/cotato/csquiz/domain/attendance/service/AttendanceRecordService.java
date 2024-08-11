package org.cotato.csquiz.domain.attendance.service;

import static org.cotato.csquiz.domain.attendance.util.AttendanceUtil.getAttendanceStatus;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceParams;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceStatistic;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceOpenStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberService memberService;
    private final RequestAttendanceService requestAttendanceService;

    public List<AttendanceRecordResponse> generateAttendanceResponses(List<Attendance> attendances) {
        List<AttendanceRecord> records = attendanceRecordRepository.findAllByAttendanceIdsInQuery(
                attendances);

        Map<Long, List<AttendanceRecord>> recordsByMemberId = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getMemberId));

        Map<Long, Member> memberMap = memberService.findActiveMember().stream()
                .collect(Collectors.toMap(Member::getId, member -> member));

        return recordsByMemberId.keySet().stream()
                .filter(memberMap::containsKey)
                .map(memberId -> AttendanceRecordResponse.of(memberMap.get(memberId),
                        AttendanceStatistic.of(recordsByMemberId.get(memberId), attendances.size())))
                .toList();
    }

    @Transactional
    public AttendResponse submitRecord(AttendanceParams request, final Long memberId) {
        Attendance attendance = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다."));

        // 해당 출석이 열려있는지 확인, 닫혀있으면 제외
        if (getAttendanceStatus(attendance, request.requestTime()) == AttendanceOpenStatus.CLOSED) {
            throw new AppException(ErrorCode.ATTENDANCE_CLOSED);
        }

        // 기존 출결 데이터가 존재하는지 확인
        if (attendanceRecordRepository.existsByAttendanceIdAndMemberIdAndAttendanceType(request.attendanceId(),
                memberId, request.attendanceType())) {
            throw new AppException(ErrorCode.ALREADY_ATTEND);
        }

        return requestAttendanceService.attend(request, memberId, attendance);
    }

    @Transactional
    public void updateAttendanceStatus(Attendance attendance) {
        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findAllByAttendanceId(attendance.getId());

        for (AttendanceRecord attendanceRecord : attendanceRecords) {
            AttendanceStatus attendanceStatus = AttendanceUtil.calculateAttendanceStatus(attendance, attendanceRecord.getAttendTime());
            attendanceRecord.updateAttendanceStatus(attendanceStatus);
        }

        attendanceRecordRepository.saveAll(attendanceRecords);
    }
}
