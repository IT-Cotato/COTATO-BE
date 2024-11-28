package org.cotato.csquiz.api.attendance.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;

public record AttendanceStatistic(
        long online,
        long offline,
        long late,
        long absent
) {
    private static final Long ZERO_VALUE = 0L;

    public static AttendanceStatistic of(List<AttendanceRecord> attendanceRecords, Integer totalAttendanceCount) {
        Map<AttendanceResult, Long> attendanceRecordsByResult = attendanceRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getAttendanceResult, Collectors.counting()));

        return new AttendanceStatistic(
                attendanceRecordsByResult.getOrDefault(AttendanceResult.ONLINE, ZERO_VALUE),
                attendanceRecordsByResult.getOrDefault(AttendanceResult.OFFLINE, ZERO_VALUE),
                attendanceRecordsByResult.getOrDefault(AttendanceResult.LATE, ZERO_VALUE),
                totalAttendanceCount - getPresentCount(attendanceRecords)
        );
    }

    private static long getPresentCount(List<AttendanceRecord> attendanceRecords) {
        return attendanceRecords.stream()
                .filter(AttendanceRecord::isPresent)
                .count();
    }
}
