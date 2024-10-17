package org.cotato.csquiz.domain.attendance.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.cotato.csquiz.domain.generation.entity.Session;

public class AttendanceExcelUtil {

    public static String generateDynamicFileName(List<Session> sessions) {
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
}
