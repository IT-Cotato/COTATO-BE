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
import org.cotato.csquiz.api.attendance.dto.GenerationMemberAttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceStatistic;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordResult;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRecordRepository;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceExcelUtil;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttendanceAdminService {

    private final GenerationReader generationReader;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
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

    @Transactional
    public void updateAttendanceRecords(Long attendanceId, Long memberId, AttendanceRecordResult attendanceResult) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다"));

        AttendanceRecord memberAttendanceRecord = attendanceRecordRepository.findByMemberIdAndAttendanceId(memberId, attendanceId)
                .orElseGet(() -> AttendanceRecord.absentRecord(attendance, memberId));

        memberAttendanceRecord.updateByAttendanceRecordResult(attendanceResult);
        attendanceRecordRepository.save(memberAttendanceRecord);
    }

    public List<GenerationMemberAttendanceRecordResponse> findAttendanceRecords(Long generationId) {
        List<Long> sessionIds = sessionRepository.findAllByGenerationId(generationId).stream().map(Session::getId).toList();
        List<Attendance> attendances = attendanceRepository.findAllBySessionIdsInQuery(sessionIds);
        Generation generation = generationReader.findById(generationId);

        return attendanceRecordService.generateAttendanceResponses(attendances, generation);
    }

    public List<AttendanceRecordResponse> findAttendanceRecordsByAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석이 존재하지 않습니다"));
        Session session = sessionRepository.findById(attendance.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));

        return attendanceRecordService.generateSingleAttendanceResponses(attendance, session.getGeneration());
    }

    public byte[] createExcelForSessionAttendance(List<Long> attendanceIds) {
        List<Member> activeMembers = memberService.findActiveMember();
        Map<Long, String> memberNameByMemberId = activeMembers.stream()
                .collect(Collectors.toMap(Member::getId, Member::getName));

        Map<String, String> columnNameBySession = new LinkedHashMap<>(generateSessionColumns(attendanceIds));
        LinkedHashMap<Long, Map<String, String>> attendanceStatusBySessionByMemberId = generateAttendanceStatusBySessionByMemberId(
                attendanceIds,
                activeMembers);

        LinkedHashMap<Long, Map<String, Integer>> attendanceCountByAttendanceStatusByMemberId = generateAttendanceCounts(
                attendanceStatusBySessionByMemberId,
                columnNameBySession);

        return AttendanceExcelUtil.createExcelFile(columnNameBySession, attendanceStatusBySessionByMemberId,
                memberNameByMemberId,
                attendanceCountByAttendanceStatusByMemberId);
    }

    public String getEncodedFileName(List<Long> attendanceIds) {
        List<Attendance> attendances = attendanceRepository.findAllById(attendanceIds);
        List<Long> sessionIds = attendances.stream()
                .map(Attendance::getSessionId)
                .toList();
        List<Session> sessions = sessionRepository.findAllById(sessionIds);

        String dynamicFileName = AttendanceExcelUtil.createDynamicFileName(sessions);
        return AttendanceExcelUtil.getEncodedFileName(dynamicFileName);
    }

    private Map<String, String> generateSessionColumns(List<Long> attendanceIds) {
        Map<String, String> columnNameBySession = new LinkedHashMap<>();
        List<Attendance> attendances = attendanceRepository.findAllById(attendanceIds);

        for (Attendance attendance : attendances) {
            String columnName = generateSessionColumnName(attendance.getSessionId());
            columnNameBySession.put(columnName, columnName);
        }
        return columnNameBySession;
    }

    private String generateSessionColumnName(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션 정보를 찾을 수 없습니다."));

        String sessionDate = session.getSessionDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return session.getNumber() + "주차 세션 (" + sessionDate + ")";
    }

    private LinkedHashMap<Long, Map<String, String>> generateAttendanceStatusBySessionByMemberId(
            List<Long> attendanceIds,
            List<Member> activeMembers) {
        LinkedHashMap<Long, Map<String, String>> attendanceStatusBySessionByMemberId = new LinkedHashMap<>();
        activeMembers.forEach(member -> attendanceStatusBySessionByMemberId.put(member.getId(), new LinkedHashMap<>()));

        List<Attendance> attendances = attendanceRepository.findAllById(attendanceIds);
        for (Attendance attendance : attendances) {
            String columnName = generateSessionColumnName(attendance.getSessionId());
            generateExcelAttendanceRecordsData(attendance.getId(), attendanceStatusBySessionByMemberId, columnName,
                    activeMembers);
        }

        return attendanceStatusBySessionByMemberId;
    }

    private void generateExcelAttendanceRecordsData(Long attendanceId,
                                                    LinkedHashMap<Long, Map<String, String>> attendanceStatusBySessionByMemberId,
                                                    String columnName, List<Member> allMembers) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 출석 정보가 존재하지 않습니다."));

        List<AttendanceRecordResponse> attendanceRecords = attendanceRecordService.generateAttendanceResponses(
                List.of(attendance));

        for (AttendanceRecordResponse record : attendanceRecords) {
            Long memberId = record.memberInfo().memberId();
            String attendanceStatus = getAttendanceStatus(record.statistic());
            attendanceStatusBySessionByMemberId
                    .computeIfAbsent(memberId, k -> new LinkedHashMap<>())
                    .put(columnName, attendanceStatus);
        }

        for (Member member : allMembers) {
            attendanceStatusBySessionByMemberId
                    .computeIfAbsent(member.getId(), k -> new LinkedHashMap<>())
                    .putIfAbsent(columnName, AttendanceResult.ABSENT.getDescription());
        }
    }

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

    private LinkedHashMap<Long, Map<String, Integer>> generateAttendanceCounts(
            LinkedHashMap<Long, Map<String, String>> attendanceStatusBySessionByMemberId,
            Map<String, String> columnNameBySession) {

        LinkedHashMap<Long, Map<String, Integer>> attendanceCountByAttendanceStatusByMemberId = new LinkedHashMap<>();

        for (Map.Entry<Long, Map<String, String>> entry : attendanceStatusBySessionByMemberId.entrySet()) {
            Long memberId = entry.getKey();
            Map<String, String> sessionStatus = entry.getValue();

            int totalAttendance = 0;
            int totalOffline = 0;
            int totalOnline = 0;
            int totalLate = 0;
            int totalAbsent = 0;

            for (String columnName : columnNameBySession.keySet()) {
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

            Map<String, Integer> attendanceCounts = new LinkedHashMap<>();
            attendanceCounts.put("totalAttendance", totalAttendance);
            attendanceCounts.put("totalOffline", totalOffline);
            attendanceCounts.put("totalOnline", totalOnline);
            attendanceCounts.put("totalLate", totalLate);
            attendanceCounts.put("totalAbsent", totalAbsent);

            attendanceCountByAttendanceStatusByMemberId.put(memberId, attendanceCounts);
        }

        return attendanceCountByAttendanceStatusByMemberId;
    }
}
