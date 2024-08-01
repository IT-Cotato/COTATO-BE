package org.cotato.csquiz.domain.attendance.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final MemberService memberService;

    public List<AttendanceRecordResponse> generateAttendanceResponses(List<Attendance> attendances){
        Map<Long, List<AttendanceRecord>> recordsByMemberId = attendanceRecordRepository.findAllByAttendanceIdsInQuery(
                        attendances).stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getMemberId));

        return recordsByMemberId.entrySet().stream()
                .map(entry -> {
                    MemberInfoResponse memberInfo = memberService.findMemberInfo(entry.getKey());
                    return AttendanceRecordResponse.of(memberInfo, entry.getValue(), attendances.size());
                })
                .toList();
    }
}
