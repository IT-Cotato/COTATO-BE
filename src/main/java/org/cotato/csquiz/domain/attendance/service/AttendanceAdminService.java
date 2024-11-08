package org.cotato.csquiz.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceStatistic;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceExcelUtil;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttendanceAdminService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordService attendanceRecordService;
    private final SessionRepository sessionRepository;
    private final MemberService memberService;

    @Transactional
    public void addAttendance(Session session, Location location, LocalTime attendanceDeadline,
                              LocalTime lateDeadline) {
        AttendanceUtil.validateAttendanceTime(session.getSessionDateTime(), attendanceDeadline, lateDeadline);

        Attendance attendance = Attendance.builder()
                .session(session)
                .location(location)
                .attendanceDeadLine(LocalDateTime.of(session.getSessionDateTime().toLocalDate(), attendanceDeadline))
                .lateDeadLine(LocalDateTime.of(session.getSessionDateTime().toLocalDate(), lateDeadline))
                .build();

        attendanceRepository.save(attendance);
    }

    @Transactional
    public void updateAttendanceByAttendanceId(UpdateAttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new EntityNotFoundException("해당 출석 정보가 존재하지 않습니다"));
        Session attendanceSession = sessionRepository.findById(attendance.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("출석과 연결된 세션을 찾을 수 없습니다"));

        updateAttendance(attendanceSession, attendance, request.attendTime(), request.location());
    }

    @Transactional
    public void updateAttendance(Session attendanceSession, Attendance attendance,
                                 AttendanceDeadLineDto attendanceDeadLine, Location location) {
        AttendanceUtil.validateAttendanceTime(attendanceSession.getSessionDateTime(),
                attendanceDeadLine.attendanceDeadLine(),
                attendanceDeadLine.lateDeadLine());

        // 세션 날짜가 존재하지 않는 경우 예외 발생
        if (attendanceSession.getSessionDateTime() == null) {
            throw new AppException(ErrorCode.SESSION_DATE_NOT_FOUND);
        }

        attendance.updateDeadLine(LocalDateTime.of(attendanceSession.getSessionDateTime().toLocalDate(),
                        attendanceDeadLine.attendanceDeadLine()),
                LocalDateTime.of(attendanceSession.getSessionDateTime().toLocalDate(),
                        attendanceDeadLine.lateDeadLine()));
        attendance.updateLocation(location);

        attendanceRecordService.updateAttendanceStatus(attendanceSession.getSessionDateTime(), attendance);
    }

    public List<AttendanceRecordResponse> findAttendanceRecords(Long generationId, Integer month) {
        List<Session> sessions = sessionRepository.findAllByGenerationId(generationId);
        if (month != null) {
            sessions = sessions.stream()
                    .filter(session -> session.getSessionDateTime().getMonthValue() == month)
                    .toList();
        }
        List<Long> sessionIds = sessions.stream()
                .map(Session::getId)
                .toList();

        List<Attendance> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds);

        return attendanceRecordService.generateAttendanceResponses(attendances);
    }

    public List<AttendanceRecordResponse> findAttendanceRecordsByAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다"));

        return attendanceRecordService.generateAttendanceResponses(List.of(attendance));
    }

    public byte[] createExcelForSessionAttendance(List<Long> attendanceIds) {
        // 활동 부원 목록을 한 번만 가져와 고정
        List<Member> activeMembers = memberService.findActiveMember();
        Map<Long, String> memberNameMap = activeMembers.stream()
                .collect(Collectors.toMap(Member::getId, Member::getName));

        LinkedHashMap<String, String> sessionColumnNames = generateSessionColumns(attendanceIds);
        LinkedHashMap<Long, Map<String, String>> memberStatisticsMap = generateMemberStatisticsMap(attendanceIds,
                activeMembers);

        // 출석 상태 카운트를 사전에 생성하여 전달
        LinkedHashMap<Long, int[]> attendanceCountsMap = generateAttendanceCounts(memberStatisticsMap,
                sessionColumnNames);

        // 엑셀 파일 생성 및 반환
        return AttendanceExcelUtil.createExcelFile(sessionColumnNames, memberStatisticsMap, memberNameMap,
                attendanceCountsMap);
    }

    public String getEncodedFileName(List<Long> attendanceIds) {
        List<Attendance> attendances = attendanceRepository.findAllById(attendanceIds);
        List<Long> sessionIds = attendances.stream()
                .map(Attendance::getSessionId)
                .distinct()
                .toList();
        List<Session> sessions = sessionRepository.findAllById(sessionIds);

        String dynamicFileName = AttendanceExcelUtil.createDynamicFileName(sessions); // 파일명 생성
        return AttendanceExcelUtil.getEncodedFileName(dynamicFileName);  // 인코딩 처리
    }

    private LinkedHashMap<String, String> generateSessionColumns(List<Long> attendanceIds) {
        LinkedHashMap<String, String> sessionColumnNames = new LinkedHashMap<>();
        List<Attendance> attendances = attendanceRepository.findAllById(attendanceIds);

        for (Attendance attendance : attendances) {
            String columnName = generateSessionColumnName(attendance.getSessionId());
            sessionColumnNames.put(columnName, columnName);
        }
        return sessionColumnNames;
    }

    private String generateSessionColumnName(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션 정보를 찾을 수 없습니다."));

        String sessionDate = session.getSessionDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return session.getNumber() + "주차 세션 (" + sessionDate + ")";
    }

    // 멤버 출석 통계를 생성하는 메서드
    private LinkedHashMap<Long, Map<String, String>> generateMemberStatisticsMap(List<Long> attendanceIds,
                                                                                 List<Member> activeMembers) {
        LinkedHashMap<Long, Map<String, String>> memberStatisticsMap = new LinkedHashMap<>();
        activeMembers.forEach(member -> memberStatisticsMap.put(member.getId(), new LinkedHashMap<>()));

        List<Attendance> attendances = attendanceRepository.findAllById(attendanceIds);
        for (Attendance attendance : attendances) {
            String columnName = generateSessionColumnName(attendance.getSessionId());
            generateExcelAttendanceRecordsData(attendance.getId(), memberStatisticsMap, columnName, activeMembers);
        }

        return memberStatisticsMap;
    }

    // 실제 출석 기록을 업데이트하는 메소드
    private void generateExcelAttendanceRecordsData(Long attendanceId,
                                                    LinkedHashMap<Long, Map<String, String>> memberStatisticsMap,
                                                    String columnName, List<Member> allMembers) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석 정보가 존재하지 않습니다."));

        List<AttendanceRecordResponse> attendanceRecords = attendanceRecordService.generateAttendanceResponses(
                List.of(attendance));

        // 출석 기록이 있는 회원들의 출석 상태 업데이트
        for (AttendanceRecordResponse record : attendanceRecords) {
            Long memberId = record.memberInfo().memberId();
            String attendanceStatus = getAttendanceStatus(record.statistic());
            memberStatisticsMap
                    .computeIfAbsent(memberId, k -> new LinkedHashMap<>())
                    .put(columnName, attendanceStatus);
        }

        // 출석 기록이 없는 회원들의 출석 상태를 '결석'으로 설정
        for (Member member : allMembers) {
            memberStatisticsMap
                    .computeIfAbsent(member.getId(), k -> new LinkedHashMap<>())
                    .putIfAbsent(columnName, AttendanceResult.ABSENT.getDescription());
        }
    }

    // 출석 상태를 결정하는 함수
    private String getAttendanceStatus(AttendanceStatistic statistic) {
        if (statistic == null) {
            return AttendanceResult.ABSENT.getDescription();
        }

        if (statistic.offline() > 0) {
            return AttendanceType.OFFLINE.getDescription();
        }
        if (statistic.online() > 0) {
            return AttendanceType.ONLINE.getDescription();
        }
        if (statistic.late() > 0) {
            return AttendanceResult.LATE.getDescription();
        }
        return AttendanceResult.ABSENT.getDescription();
    }

    private LinkedHashMap<Long, int[]> generateAttendanceCounts(
            LinkedHashMap<Long, Map<String, String>> memberStatisticsMap,
            LinkedHashMap<String, String> sessionColumnNames) {

        LinkedHashMap<Long, int[]> attendanceCountsMap = new LinkedHashMap<>();

        for (Long memberId : memberStatisticsMap.keySet()) {
            int totalAttendance = 0;
            int totalOffline = 0;
            int totalOnline = 0;
            int totalLate = 0;
            int totalAbsent = 0;

            Map<String, String> sessionStatus = memberStatisticsMap.get(memberId);

            for (String columnName : sessionColumnNames.keySet()) {
                String status = sessionStatus.getOrDefault(columnName, AttendanceResult.ABSENT.getDescription());

                if (status.equals(AttendanceType.OFFLINE.getDescription())) {
                    totalAttendance++;
                    totalOffline++;
                } else if (status.equals(AttendanceType.ONLINE.getDescription())) {
                    totalAttendance++;
                    totalOnline++;
                } else if (status.equals(AttendanceResult.LATE.getDescription())) {
                    totalLate++;
                } else if (status.equals(AttendanceResult.ABSENT.getDescription())) {
                    totalAbsent++;
                }
            }

            attendanceCountsMap.put(memberId,
                    new int[]{totalAttendance, totalOffline, totalOnline, totalLate, totalAbsent});
        }

        return attendanceCountsMap;
    }
}
