package org.cotato.csquiz.domain.attendance.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceStatistic;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.auth.entity.Member;
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

    public List<AttendanceRecordResponse> generateAttendanceResponses(Attendance attendance) {
        return generateAttendanceResponses(List.of(attendance));
    }

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
}
