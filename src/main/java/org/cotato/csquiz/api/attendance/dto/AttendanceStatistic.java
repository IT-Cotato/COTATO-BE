package org.cotato.csquiz.api.attendance.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

public record AttendanceStatistic(
        Integer online,
        Integer offline,
        Integer late,
        Integer absent
) {
    public static AttendanceStatistic of(List<AttendanceRecord> attendanceRecords, Integer totalAttendance) {
        Map<AttendanceResult, List<AttendanceRecord>> countByStatus = attendanceRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getAttendanceResult));
        List<AttendanceRecord> presentRecords = countByStatus.getOrDefault(AttendanceResult.PRESENT, List.of());

        int onlineCount = (int) presentRecords.stream()
                .filter(record -> AttendanceType.ONLINE == record.getAttendanceType())
                .count();
        int offLineCount = (int) presentRecords.stream()
                .filter(record -> AttendanceType.OFFLINE == record.getAttendanceType())
                .count();

        return new AttendanceStatistic(
                onlineCount,
                offLineCount,
                countByStatus.getOrDefault(AttendanceResult.LATE, List.of()).size(),
                totalAttendance - attendanceRecords.size()
        );
    }
}
