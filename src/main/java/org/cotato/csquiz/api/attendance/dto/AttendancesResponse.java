package org.cotato.csquiz.api.attendance.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;

@Builder
public record AttendancesResponse(
	@Schema(requiredMode = RequiredMode.REQUIRED)
	Long generationId,
	@Schema(requiredMode = RequiredMode.REQUIRED)
	Long generationNumber,
	List<AttendanceWithSessionResponse> attendances
) {
}
