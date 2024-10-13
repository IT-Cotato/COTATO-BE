package org.cotato.csquiz.domain.attendance.service;


import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.cotato.csquiz.domain.attendance.repository.AttendanceRepository;
import org.cotato.csquiz.domain.attendance.util.AttendanceUtil;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<byte[]> exportAttendanceRecordsToExcelBySessions(List<Long> sessionIds) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // 세션별 출석 데이터를 저장할 구조체
            Map<String, Map<String, String>> memberStatisticsMap = new HashMap<>();
            LinkedHashMap<String, String> sessionColumnNames = new LinkedHashMap<>(); // 주차별 세션 이름 저장 (순서 유지)
            List<Session> sessions = new ArrayList<>();

            // 1. 모든 세션에 대해 회원 출결 정보 수집
            for (Long sessionId : sessionIds) {
                Session session = sessionRepository.findById(sessionId)
                        .orElseThrow(() -> new EntityNotFoundException("세션을 찾을 수 없습니다: " + sessionId));
                sessions.add(session);

                // 세션 날짜와 주차 정보를 사용하여 열 이름 생성
                String sessionDate = session.getSessionDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String columnName = session.getNumber() + "주차 세션 (" + sessionDate + ")";
                sessionColumnNames.put(columnName, sessionDate);

                // 2. 해당 세션에 대해 모든 회원의 출석 상태를 '결석'으로 초기화
                List<String> allMemberNames = memberService.findActiveMember().stream()
                        .map(Member::getName)
                        .toList();// 모든 회원 이름 가져오기

                for (String memberName : allMemberNames) {
                    memberStatisticsMap
                            .computeIfAbsent(memberName, k -> new HashMap<>())
                            .put(columnName, "결석");
                }

                // 3. 해당 세션의 실제 출석 기록을 가져와 상태 업데이트
                List<Attendance> attendances = attendanceRepository.findAllBySessionIdsInQuery(List.of(sessionId));
                List<AttendanceRecordResponse> attendanceRecords = attendanceRecordService.generateAttendanceResponses(
                        attendances);

                // 실제 출석 기록이 있는 회원들의 상태를 업데이트
                for (AttendanceRecordResponse record : attendanceRecords) {
                    String memberName = record.memberInfo().name();
                    String attendanceStatus = determineAttendanceStatus(record.statistic());

                    // 이미 결석으로 초기화된 상태를 실제 출석 상태로 덮어씀
                    memberStatisticsMap.get(memberName).put(columnName, attendanceStatus);
                }
            }

            // 엑셀 시트 생성
            Sheet sheet = workbook.createSheet("Attendance Summary");

            // 첫 번째 행(헤더)에 세션별로 열 이름 추가
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("부원이름");
            int colNum = 1;

            // 세션별로 주차 및 날짜를 열 이름으로 추가
            for (String columnName : sessionColumnNames.keySet()) {
                headerRow.createCell(colNum++).setCellValue(columnName);
            }
            headerRow.createCell(colNum++).setCellValue("출석");
            headerRow.createCell(colNum++).setCellValue("대면");
            headerRow.createCell(colNum++).setCellValue("비대면");
            headerRow.createCell(colNum++).setCellValue("지각");
            headerRow.createCell(colNum++).setCellValue("결석");

            // 데이터 추가 (회원별 출석 기록을 행으로 추가)
            int rowNum = 1;
            for (Map.Entry<String, Map<String, String>> entry : memberStatisticsMap.entrySet()) {
                String memberName = entry.getKey();
                Map<String, String> sessionStats = entry.getValue();

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(memberName);

                // 출석 상태를 계산하기 위한 카운터 변수들
                int totalAttendance = 0;
                int totalOffline = 0;
                int totalOnline = 0;
                int totalLate = 0;
                int totalAbsent = 0;

                colNum = 1;

                // 각 세션별 출석 상태를 열에 기록
                for (String columnName : sessionColumnNames.keySet()) {
                    String status = sessionStats.get(columnName);
                    row.createCell(colNum++).setCellValue(status);

                    // 상태에 따라 카운트
                    switch (status) {
                        case "대면(출석)" -> {
                            totalAttendance++;
                            totalOffline++;
                        }
                        case "비대면(출석)" -> {
                            totalAttendance++;
                            totalOnline++;
                        }
                        case "지각" -> totalLate++;
                        case "결석" -> totalAbsent++;
                    }
                }

                // 각 회원의 출석 카운트 추가
                row.createCell(colNum++).setCellValue(totalAttendance);
                row.createCell(colNum++).setCellValue(totalOffline);
                row.createCell(colNum++).setCellValue(totalOnline);
                row.createCell(colNum++).setCellValue(totalLate);
                row.createCell(colNum++).setCellValue(totalAbsent);
            }

            // 엑셀 파일을 ByteArrayOutputStream에 저장
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();

            // 파일 다운로드를 위한 ResponseEntity 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_summary.xlsx");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(bytes);

        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_GENERATION_FAIL);
        }
    }

    // 출석 상태를 결정하는 함수
    private String determineAttendanceStatus(AttendanceStatistic statistic) {
        if (statistic.offline() > 0) {
            return "대면(출석)";
        } else if (statistic.online() > 0) {
            return "비대면(출석)";
        } else if (statistic.late() > 0) {
            return "지각";
        } else {
            return "결석";
        }
    }
}
