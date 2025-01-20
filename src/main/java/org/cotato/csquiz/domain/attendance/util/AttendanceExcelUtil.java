package org.cotato.csquiz.domain.attendance.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.cotato.csquiz.domain.generation.entity.Generation;

public class AttendanceExcelUtil {

    public static String getGenerationRecordExcelFileName(final Generation generation) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년MM월dd일");
        return String.format("%s기-출석-현황-%s기준", generation.getNumber(), now.format(formatter));
    }
}
