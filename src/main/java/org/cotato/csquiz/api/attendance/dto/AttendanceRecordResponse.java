package org.cotato.csquiz.api.attendance.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.attendance.enums.AttendanceStatus;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRole;


public record AttendanceRecordResponse(
        AttendanceMemberInfo memberInfo,
        List<AttendanceInfoResponse> attendanceInfos,
        AttendanceStatistic statistic
) {
    public static AttendanceRecordResponse of(Member member, List<AttendanceRecord> attendanceRecords,
                                              Integer totalAttendance) {
        return new AttendanceRecordResponse(
                AttendanceMemberInfo.from(member),
                attendanceRecords.stream()
                        .map(AttendanceInfoResponse::from)
                        .toList(),
                AttendanceStatistic.from(attendanceRecords, totalAttendance)
        );
    }

    public record AttendanceMemberInfo(
            Long memberId,
            String memberName,
            MemberRole role
    ){
        static AttendanceMemberInfo from(Member member) {
            return new AttendanceMemberInfo(
                    member.getId(),
                    member.getName(),
                    member.getRole()
            );
        }
    }

    public record AttendanceStatistic(
            Integer onLine,
            Integer offLine,
            Integer late,
            Integer absent
    ) {
        public static AttendanceStatistic from(List<AttendanceRecord> attendanceRecords, Integer totalAttendance) {
            Map<AttendanceStatus, List<AttendanceRecord>> countByStatus = attendanceRecords.stream()
                    .collect(Collectors.groupingBy(AttendanceRecord::getAttendanceStatus));
            List<AttendanceRecord> presentRecords = countByStatus.getOrDefault(AttendanceStatus.PRESENT, List.of());

            int onlineCount = (int) presentRecords.stream()
                    .filter(record -> AttendanceType.ONLINE == record.getAttendanceType())
                    .count();
            int offLineCount = (int) presentRecords.stream()
                    .filter(record -> AttendanceType.OFFLINE == record.getAttendanceType())
                    .count();

            return new AttendanceStatistic(
                    onlineCount,
                    offLineCount,
                    countByStatus.getOrDefault(AttendanceStatus.LATE, List.of()).size(),
                    totalAttendance - attendanceRecords.size()
            );
        }
    }
}
