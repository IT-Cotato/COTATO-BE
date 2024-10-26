package org.cotato.csquiz.domain.attendance.service;


import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

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

    public byte[] exportAttendanceRecordsToExcelBySessions(List<Long> sessionIds) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // 세션별 출석 데이터를 저장할 구조체
            Map<Long, Map<String, String>> memberStatisticsMap = new HashMap<>();
            LinkedHashMap<String, String> sessionColumnNames = new LinkedHashMap<>();

            // 모든 세션에 대한 출석 정보 수집
            collectAttendanceRecords(sessionIds, memberStatisticsMap, sessionColumnNames);

            // 엑셀 파일 생성 및 데이터 추가
            return generateExcelFile(workbook, sessionColumnNames, memberStatisticsMap);

        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_GENERATION_FAIL);
        }
    }

    public String getEncodedFileName(List<Long> sessionIds) {
        List<Session> sessions = sessionRepository.findAllById(sessionIds);
        String dynamicFileName = AttendanceExcelUtil.generateDynamicFileName(sessions); // 파일명 생성
        return AttendanceExcelUtil.getEncodedFileName(dynamicFileName);  // 인코딩 처리
    }

    // 출석 정보를 수집하는 메소드
    private void collectAttendanceRecords(List<Long> sessionIds, Map<Long, Map<String, String>> memberStatisticsMap,
                                          LinkedHashMap<String, String> sessionColumnNames) {
        // 활동 중인 멤버 목록을 한 번만 쿼리
        List<Member> allMembers = memberService.findActiveMember();

        for (Long sessionId : sessionIds) {
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException("세션을 찾을 수 없습니다: " + sessionId));

            // 세션 이름을 생성하고 열 이름에 추가
            String sessionDate = session.getSessionDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String columnName = session.getNumber() + "주차 세션 (" + sessionDate + ")";
            sessionColumnNames.put(columnName, sessionDate);

            // 회원들의 출석 기록을 업데이트 하고 기록이 없을 경우 일괄 '결석' 처리
            updateAttendanceRecords(sessionId, memberStatisticsMap, columnName, allMembers);
        }
    }

    // 실제 출석 기록을 업데이트하는 메소드
    private void updateAttendanceRecords(Long sessionId, Map<Long, Map<String, String>> memberStatisticsMap,
                                         String columnName, List<Member> allMembers) {
        List<Attendance> attendances = attendanceRepository.findAllBySessionId(sessionId);
        List<AttendanceRecordResponse> attendanceRecords = attendanceRecordService.generateAttendanceResponses(
                attendances);

        // 출석 기록이 있는 회원들의 출석 상태 업데이트
        for (AttendanceRecordResponse record : attendanceRecords) {
            Long memberId = record.memberInfo().memberId();
            String attendanceStatus = determineAttendanceStatus(record.statistic());
            memberStatisticsMap
                    .computeIfAbsent(memberId, k -> new HashMap<>())
                    .put(columnName, attendanceStatus);
        }

        // 출석 기록이 없는 회원들의 출석 상태를 '결석'으로 설정
        for (Member member : allMembers) {
            memberStatisticsMap
                    .computeIfAbsent(member.getId(), k -> new HashMap<>())
                    .putIfAbsent(columnName, AttendanceResult.ABSENT.getDescription());
        }
    }

    // 엑셀 파일을 생성하고 데이터를 입력하는 메소드
    private byte[] generateExcelFile(Workbook workbook, LinkedHashMap<String, String> sessionColumnNames,
                                     Map<Long, Map<String, String>> memberStatisticsMap) throws IOException {
        Sheet sheet = workbook.createSheet();

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        createHeaderRow(headerRow, sessionColumnNames);

        // 데이터 생성
        int rowNum = 1;
        for (Map.Entry<Long, Map<String, String>> entry : memberStatisticsMap.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Long memberId = entry.getKey();
            String memberName = memberRepository.findById(memberId)
                    .map(Member::getName)
                    .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다: " + memberId));
            addMemberAttendanceData(row, memberName, entry.getValue(), sessionColumnNames);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }

    // 헤더 행을 생성하는 메소드
    private void createHeaderRow(Row headerRow, LinkedHashMap<String, String> sessionColumnNames) {
        headerRow.createCell(0).setCellValue(MemberRoleGroup.ACTIVE_MEMBERS.getDescription());
        int colNum = 1;

        for (String columnName : sessionColumnNames.keySet()) {
            headerRow.createCell(colNum++).setCellValue(columnName);
        }

        headerRow.createCell(colNum++).setCellValue(AttendanceResult.PRESENT.getDescription());
        headerRow.createCell(colNum++).setCellValue(AttendanceType.OFFLINE.getDescription());
        headerRow.createCell(colNum++).setCellValue(AttendanceType.ONLINE.getDescription());
        headerRow.createCell(colNum++).setCellValue(AttendanceResult.LATE.getDescription());
        headerRow.createCell(colNum).setCellValue(AttendanceResult.ABSENT.getDescription());
    }

    // 회원의 출석 데이터를 추가하는 메서드
    private void addMemberAttendanceData(Row row, String memberName, Map<String, String> sessionStats,
                                         LinkedHashMap<String, String> sessionColumnNames) {
        // 회원 이름을 첫 번째 열에 추가
        row.createCell(0).setCellValue(memberName);

        // 세션 데이터는 두 번째 열부터 시작
        int colNum = 1;

        int totalAttendance = 0;
        int totalOffline = 0;
        int totalOnline = 0;
        int totalLate = 0;
        int totalAbsent = 0;

        for (String columnName : sessionColumnNames.keySet()) {
            String status = sessionStats.get(columnName);
            row.createCell(colNum++).setCellValue(status);

            // 출석 상태에 따라 카운트 처리 (적절한 Enum을 사용)
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

        // 출석 카운트 데이터를 추가
        row.createCell(colNum++).setCellValue(totalAttendance);
        row.createCell(colNum++).setCellValue(totalOffline);
        row.createCell(colNum++).setCellValue(totalOnline);
        row.createCell(colNum++).setCellValue(totalLate);
        row.createCell(colNum).setCellValue(totalAbsent);
    }

    // 출석 상태를 결정하는 함수
    private String determineAttendanceStatus(AttendanceStatistic statistic) {
        if (statistic.offline() > 0) {
            return AttendanceType.OFFLINE.getDescription();
        } else if (statistic.online() > 0) {
            return AttendanceType.ONLINE.getDescription();
        } else if (statistic.late() > 0) {
            return AttendanceResult.LATE.getDescription();
        } else {
            return AttendanceResult.ABSENT.getDescription();
        }
    }
}
