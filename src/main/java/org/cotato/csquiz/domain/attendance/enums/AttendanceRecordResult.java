package org.cotato.csquiz.domain.attendance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttendanceRecordResult {
    OFFLINE("대면 출석"),
    ONLINE("비대면 출석"),
    LATE("지각"),
    ABSENT("결석"),
    ;

    private final String description;

    //AttendanceRecord의 출석정보가 AttendanceRecordResult로 바뀌면 로직 수정 TODO
    public static AttendanceRecordResult convertWithTypeAndResult(AttendanceType type, AttendanceResult result) {
        if (result == AttendanceResult.ABSENT) {
            return AttendanceRecordResult.ABSENT;
        }
        if (result == AttendanceResult.LATE) {
            return AttendanceRecordResult.LATE;
        }
        if (type == AttendanceType.ONLINE) {
            return AttendanceRecordResult.ONLINE;
        }
        return AttendanceRecordResult.OFFLINE;
    }
}
