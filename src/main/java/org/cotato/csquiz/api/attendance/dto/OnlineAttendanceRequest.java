package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDateTime;

import org.cotato.csquiz.domain.attendance.enums.AttendanceResult;
import org.cotato.csquiz.domain.attendance.enums.AttendanceType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@NotNull
public class OnlineAttendanceRequest implements AttendanceParams {

	@Schema(description = "출석 PK")
	@NotNull
	private Long attendanceId;

	@Schema(description = "회원 요청 시간")
	private LocalDateTime requestTime;

	@Override
	public AttendanceType attendanceType() {
		return AttendanceType.ONLINE;
	}

	@Override
	public AttendanceResult attendanceResult() {
		return AttendanceResult.ONLINE;
	}

	@Override
	public Long attendanceId() {
		return attendanceId;
	}

	@Override
	public LocalDateTime requestTime() {
		return requestTime;
	}
}
