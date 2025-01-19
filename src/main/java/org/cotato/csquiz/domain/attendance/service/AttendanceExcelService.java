package org.cotato.csquiz.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceStatistic;
import org.cotato.csquiz.api.attendance.dto.GenerationMemberAttendanceRecordResponse;
import org.cotato.csquiz.common.poi.AttendanceRecordExcelData;
import org.cotato.csquiz.common.poi.AttendanceRecordExcelData.AttendRecord;
import org.cotato.csquiz.common.poi.ExcelWriter;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceReader;
import org.cotato.csquiz.domain.attendance.service.component.AttendanceRecordReader;
import org.cotato.csquiz.domain.attendance.util.AttendanceExcelUtil;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.auth.service.component.GenerationMemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.cotato.csquiz.domain.generation.service.component.SessionReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AttendanceExcelService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordService attendanceRecordService;
    private final SessionRepository sessionRepository;
    private final MemberService memberService;

    private final GenerationReader generationReader;
    private final SessionReader sessionReader;
    private final AttendanceRecordReader attendanceRecordReader;
    private final AttendanceReader attendanceReader;
    private final GenerationMemberReader generationMemberReader;

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
        Session session = sessionRepository.findById(attendance.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("출석에 대한 세션 정보를 찾을 수 없습니다."));

        List<GenerationMemberAttendanceRecordResponse> attendanceRecords = attendanceRecordService.generateAttendanceResponses(
                List.of(attendance), session.getGeneration());

        for (GenerationMemberAttendanceRecordResponse record : attendanceRecords) {
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

    public Map<String, Object> getAttendanceRecordsExcelDataByGeneration(final Long generationId) {
        Generation generation = generationReader.findById(generationId);
        Map<Long, Session> sessionById = sessionReader.findAllByGeneration(generation).stream()
                .collect(Collectors.toUnmodifiableMap(Session::getId, Function.identity()));

        List<Attendance> attendances = attendanceReader.getAllBySessions(sessionById.values());
        List<Member> members = generationMemberReader.getAllByGeneration(generation).stream()
                .map(GenerationMember::getMember).toList();

        attendances.forEach(attendanceRecordService::refreshAttendanceRecords);

        List<AttendanceRecordExcelData> excelData = getAttendanceRecordsExcelData(members, attendances, sessionById);

        Map<String, Object> datas = new HashMap<>();
        datas.put(ExcelWriter.FILE_NAME, AttendanceExcelUtil.getGenerationRecordExcelFileName(generation));
        datas.put(ExcelWriter.SHEETS, Map.of(AttendanceExcelUtil.getGenerationRecordExcelFileName(generation), excelData));

        return datas;
    }

    private List<AttendanceRecordExcelData> getAttendanceRecordsExcelData(final List<Member> members,
                                                                          final List<Attendance> attendances,
                                                                          final Map<Long, Session> sessionById) {
        Map<Long, Member> memberById = members.stream()
                .collect(Collectors.toUnmodifiableMap(Member::getId, Function.identity()));

        Map<Long, List<AttendanceRecord>> recordsByMemberId = attendanceRecordReader.getAllByAttendances(attendances).stream()
                .filter(attendanceRecord -> memberById.containsKey(attendanceRecord.getMemberId()))
                .collect(Collectors.groupingBy(AttendanceRecord::getMemberId));

        Map<Long, Session> sessionByAttendanceId = attendances.stream()
                .collect(Collectors.toMap(Attendance::getId, attendance -> sessionById.get(attendance.getSessionId())));

        return recordsByMemberId.entrySet().stream()
                .map(memberIdAndRecords -> {
                    Member member = memberById.get(memberIdAndRecords.getKey());
                    List<AttendRecord> records = memberIdAndRecords.getValue().stream()
                            .map(attendanceRecord -> AttendRecord.of(
                                    sessionByAttendanceId.get(attendanceRecord.getAttendanceId()), attendanceRecord))
                            .toList();

                    return AttendanceRecordExcelData.of(member, attendances.size(), records);
                })
                .toList();
    }
}
