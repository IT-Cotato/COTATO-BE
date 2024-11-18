package org.cotato.csquiz.domain.attendance.util;

import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceRecordCreationType;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.domain.generation.entity.Session;

public class AttendanceExcelUtil {

    private static final int DEFAULT_TOTAL_ATTENDANCE = 0;
    private static final int DEFAULT_TOTAL_OFFLINE = 0;
    private static final int DEFAULT_TOTAL_ONLINE = 0;
    private static final int DEFAULT_TOTAL_LATE = 0;
    private static final int DEFAULT_TOTAL_ABSENT = 0;

    public static String createDynamicFileName(List<Session> sessions) {
        Integer generationNumber = sessions.get(0).getGeneration().getNumber();
        List<String> sessionNumbers = sessions.stream()
                .map(Session::getNumber)
                .sorted()
                .map(String::valueOf)
                .toList();

        String sessionRange = String.join(",", sessionNumbers);
        return String.format("출결기록_%d기_%s주차_출석.xlsx", generationNumber, sessionRange);
    }

    public static String getEncodedFileName(String dynamicFileName) {
        return URLEncoder.encode(dynamicFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    public static byte[] createExcelFile(Map<String, String> columnNameBySession,
                                         LinkedHashMap<Long, Map<String, String>> attendanceStatusBySessionByMemberId,
                                         Map<Long, String> memberNameByMemberId,
                                         LinkedHashMap<Long, Map<String, Integer>> attendanceCountByAttendanceStatusByMemberId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            createHeaderRow(sheet, columnNameBySession);
            createMemberAttendanceDataRows(sheet, attendanceStatusBySessionByMemberId, columnNameBySession,
                    memberNameByMemberId,
                    attendanceCountByAttendanceStatusByMemberId);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_GENERATION_FAIL);
        }
    }

    public static void createHeaderRow(Sheet sheet, Map<String, String> columnNameBySession) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(MemberRoleGroup.ACTIVE_MEMBERS.getDescription());

        int columnNumber = 1;
        for (String columnName : columnNameBySession.keySet()) {
            headerRow.createCell(columnNumber++).setCellValue(columnName);
        }

        headerRow.createCell(columnNumber++).setCellValue(AttendanceResult.PRESENT.getDescription());
        headerRow.createCell(columnNumber++).setCellValue(AttendanceRecordCreationType.OFFLINE.getDescription());
        headerRow.createCell(columnNumber++).setCellValue(AttendanceRecordCreationType.ONLINE.getDescription());
        headerRow.createCell(columnNumber++).setCellValue(AttendanceResult.LATE.getDescription());
        headerRow.createCell(columnNumber).setCellValue(AttendanceResult.ABSENT.getDescription());
    }

    public static void createMemberAttendanceDataRows(Sheet sheet,
                                                      LinkedHashMap<Long, Map<String, String>> attendanceStatusBySessionByMemberId,
                                                      Map<String, String> columnNameBySession,
                                                      Map<Long, String> memberNameByMemberId,
                                                      LinkedHashMap<Long, Map<String, Integer>> attendanceCountByAttendanceStatusByMemberId) {
        int rowNumber = 1;

        for (Map.Entry<Long, Map<String, String>> entry : attendanceStatusBySessionByMemberId.entrySet()) {
            Long memberId = entry.getKey();
            String memberName = memberNameByMemberId.get(memberId);
            Map<String, Integer> attendanceCounts = attendanceCountByAttendanceStatusByMemberId.get(memberId);

            if (memberName == null) {
                throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다");
            }
            if (attendanceCounts == null) {
                throw new EntityNotFoundException("출석 통계 정보를 찾을 수 없습니다");
            }

            Row row = sheet.createRow(rowNumber++);
            addMemberAttendanceData(row, memberName, entry.getValue(), columnNameBySession, attendanceCounts);
        }
    }

    private static void addMemberAttendanceData(Row row, String memberName, Map<String, String> sessionStatus,
                                                Map<String, String> sessionColumnNames,
                                                Map<String, Integer> attendanceCounts) {
        row.createCell(0).setCellValue(memberName);

        int columnNumber = 1;
        for (String columnName : sessionColumnNames.keySet()) {
            String status = sessionStatus.getOrDefault(columnName, AttendanceResult.ABSENT.getDescription());
            row.createCell(columnNumber++).setCellValue(status);
        }

        row.createCell(columnNumber++)
                .setCellValue(attendanceCounts.getOrDefault("totalAttendance", DEFAULT_TOTAL_ATTENDANCE));
        row.createCell(columnNumber++)
                .setCellValue(attendanceCounts.getOrDefault("totalOffline", DEFAULT_TOTAL_OFFLINE));
        row.createCell(columnNumber++).setCellValue(attendanceCounts.getOrDefault("totalOnline", DEFAULT_TOTAL_ONLINE));
        row.createCell(columnNumber++).setCellValue(attendanceCounts.getOrDefault("totalLate", DEFAULT_TOTAL_LATE));
        row.createCell(columnNumber).setCellValue(attendanceCounts.getOrDefault("totalAbsent", DEFAULT_TOTAL_ABSENT));
    }
}
