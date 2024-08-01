package org.cotato.csquiz.api.attendance.dto;

import java.util.List;
import org.cotato.csquiz.domain.attendance.entity.AttendanceRecord;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;


public record AttendanceRecordResponse(
        AttendanceMemberInfo memberInfo,
        AttendanceStatistic statistic
) {
    public static AttendanceRecordResponse of(Member member, List<AttendanceRecord> attendanceRecords,
                                              Integer totalAttendance) {
        return new AttendanceRecordResponse(
                AttendanceMemberInfo.from(member),
                AttendanceStatistic.from(attendanceRecords, totalAttendance)
        );
    }

    public record AttendanceMemberInfo(
            Long memberId,
            String memberName,
            MemberPosition position
    ){
        static AttendanceMemberInfo from(Member member) {
            return new AttendanceMemberInfo(
                    member.getId(),
                    member.getName(),
                    member.getPosition()
            );
        }
    }
}
