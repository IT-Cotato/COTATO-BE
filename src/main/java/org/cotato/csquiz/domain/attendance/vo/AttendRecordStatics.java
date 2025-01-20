package org.cotato.csquiz.domain.attendance.vo;

import java.util.List;
import org.cotato.csquiz.domain.attendance.poi.AttendanceRecordExcelData.AttendRecord;

public record AttendRecordStatics(
        int offline,
        int online,
        int late,
        int absent
) {

    public static AttendRecordStatics from(List<AttendRecord> attendRecords) {
        int offline = 0;
        int online = 0;
        int late = 0;
        int absent = 0;

        for (AttendRecord record : attendRecords) {
            switch (record.result()) {
                case OFFLINE -> offline++;
                case ONLINE -> online++;
                case LATE -> late++;
                case ABSENT -> absent++;
            }
        }

        return new AttendRecordStatics(offline, online, late, absent);
    }
}
