package org.cotato.csquiz.api.attendance.dto;

import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.auth.entity.Member;

public record AttendanceRecordResponse(
	AttendanceMemberInfo memberInfo,
	AttendanceResult result
) {
	public static AttendanceRecordResponse of(Member member, AttendanceResult result) {
		return new AttendanceRecordResponse(
			AttendanceMemberInfo.from(member),
			result
		);
	}
}
