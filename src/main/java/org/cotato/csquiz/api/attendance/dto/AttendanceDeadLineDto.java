package org.cotato.csquiz.api.attendance.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record AttendanceDeadLineDto(
	@Schema(example = "2024-11-11T119:05:00")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime attendanceDeadLine,
	@Schema(example = "2024-11-11T19:20:00")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime lateDeadLine
) {
}
