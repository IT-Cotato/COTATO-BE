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
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.domain.generation.entity.Session;

public class AttendanceExcelUtil {

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

    // 파일명 인코딩 메서드
    public static String getEncodedFileName(String dynamicFileName) {
        return URLEncoder.encode(dynamicFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }

    // 엑셀 파일 생성 메서드
    public static byte[] createExcelFile(LinkedHashMap<String, String> sessionColumnNames,
                                         LinkedHashMap<Long, Map<String, String>> memberStatisticsMap,
                                         Map<Long, String> memberNameMap,
                                         LinkedHashMap<Long, int[]> attendanceCountsMap) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            createHeaderRow(sheet, sessionColumnNames);
            createMemberAttendanceDataRows(sheet, memberStatisticsMap, sessionColumnNames, memberNameMap,
                    attendanceCountsMap);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_GENERATION_FAIL);
        }
    }

    // 헤더 행을 생성하는 메서드
    public static void createHeaderRow(Sheet sheet, LinkedHashMap<String, String> sessionColumnNames) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue(MemberRoleGroup.ACTIVE_MEMBERS.getDescription());

        int columnNumber = 1;
        for (String columnName : sessionColumnNames.keySet()) {
            headerRow.createCell(columnNumber++).setCellValue(columnName);
        }

        headerRow.createCell(columnNumber++).setCellValue(AttendanceResult.PRESENT.getDescription());
        headerRow.createCell(columnNumber++).setCellValue(AttendanceType.OFFLINE.getDescription());
        headerRow.createCell(columnNumber++).setCellValue(AttendanceType.ONLINE.getDescription());
        headerRow.createCell(columnNumber++).setCellValue(AttendanceResult.LATE.getDescription());
        headerRow.createCell(columnNumber).setCellValue(AttendanceResult.ABSENT.getDescription());
    }

    // 데이터 행을 생성하는 메서드
    public static void createMemberAttendanceDataRows(Sheet sheet,
                                                      LinkedHashMap<Long, Map<String, String>> memberStatisticsMap,
                                                      LinkedHashMap<String, String> sessionColumnNames,
                                                      Map<Long, String> memberNameMap,
                                                      LinkedHashMap<Long, int[]> attendanceCountsMap) {
        int rowNumber = 1;

        for (Map.Entry<Long, Map<String, String>> entry : memberStatisticsMap.entrySet()) {
            Long memberId = entry.getKey();
            String memberName = memberNameMap.get(memberId);
            int[] attendanceCounts = attendanceCountsMap.get(memberId);

            if (memberName == null) {
                throw new EntityNotFoundException("회원 정보를 찾을 수 없습니다" );
            }
            if (attendanceCounts == null) {
                throw new EntityNotFoundException("출석 통계 정보를 찾을 수 없습니다");
            }

            Row row = sheet.createRow(rowNumber++);
            addMemberAttendanceData(row, memberName, entry.getValue(), sessionColumnNames,
                    attendanceCountsMap.get(memberId));
        }
    }

    // 회원의 출석 데이터를 행에 추가하는 메서드
    private static void addMemberAttendanceData(Row row, String memberName, Map<String, String> sessionStatus,
                                                Map<String, String> sessionColumnNames, int[] attendanceCounts) {
        row.createCell(0).setCellValue(memberName);

        int columnNumber = 1;
        for (String columnName : sessionColumnNames.keySet()) {
            String status = sessionStatus.getOrDefault(columnName, AttendanceResult.ABSENT.getDescription());
            row.createCell(columnNumber++).setCellValue(status);
        }

        row.createCell(columnNumber++).setCellValue(attendanceCounts[0]);
        row.createCell(columnNumber++).setCellValue(attendanceCounts[1]);
        row.createCell(columnNumber++).setCellValue(attendanceCounts[2]);
        row.createCell(columnNumber++).setCellValue(attendanceCounts[3]);
        row.createCell(columnNumber).setCellValue(attendanceCounts[4]);
    }
}
