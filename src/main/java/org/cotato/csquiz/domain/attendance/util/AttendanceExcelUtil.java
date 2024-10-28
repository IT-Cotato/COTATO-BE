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
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
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
                                         MemberRepository memberRepository) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            // 헤더 생성
            createHeaderRow(sheet, sessionColumnNames);

            // 데이터 생성
            createMemberAttendanceDataRows(sheet, memberStatisticsMap, sessionColumnNames, memberRepository);

            // 엑셀 파일을 ByteArray로 반환
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
                                                      MemberRepository memberRepository) {
        int rowNum = 1;
        for (Long memberId : memberStatisticsMap.keySet().stream().sorted().toList()) {
            Row row = sheet.createRow(rowNum++);
        int rowNumber = 1;
            Row row = sheet.createRow(rowNumber++);
            String memberName = memberRepository.findById(memberId)
                    .map(Member::getName)
                    .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다: " + memberId));

            addMemberAttendanceData(row, memberName, memberStatisticsMap.get(memberId), sessionColumnNames);
        }
    }

    // 회원의 출석 데이터를 행에 추가하는 메서드
    private static void addMemberAttendanceData(Row row, String memberName, Map<String, String> sessionStatus,
                                                Map<String, String> sessionColumnNames) {
        // 회원 이름을 첫 번째 열에 추가
        row.createCell(0).setCellValue(memberName);

        // 세션 데이터는 두 번째 열부터 시작
        int columnNumber = 1;

        int totalAttendance = 0;
        int totalOffline = 0;
        int totalOnline = 0;
        int totalLate = 0;
        int totalAbsent = 0;

        for (String columnName : sessionColumnNames.keySet()) {
            String status = sessionStatus.getOrDefault(columnName, AttendanceResult.ABSENT.getDescription());
            row.createCell(columnNumber++).setCellValue(status);

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
        row.createCell(columnNumber++).setCellValue(totalAttendance);
        row.createCell(columnNumber++).setCellValue(totalOffline);
        row.createCell(columnNumber++).setCellValue(totalOnline);
        row.createCell(columnNumber++).setCellValue(totalLate);
        row.createCell(columnNumber).setCellValue(totalAbsent);
    }
}
