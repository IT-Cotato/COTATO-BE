package org.cotato.csquiz.domain.attendance.util;

import org.springframework.http.HttpHeaders;

public class AttendanceExcelHeaderUtil {
    private static final String ATTACHMENT_FORMAT = "attachment; filename=\"%s\"";

    private AttendanceExcelHeaderUtil() {
        // Utility class
    }

    public static HttpHeaders createExcelDownloadHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, fileName));
        return headers;
    }
}
