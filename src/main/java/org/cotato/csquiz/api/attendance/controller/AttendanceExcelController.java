package org.cotato.csquiz.api.attendance.controller;

import java.util.List;

import org.cotato.csquiz.common.poi.ExcelView;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.attendance.service.AttendanceExcelService;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v2/api/attendances/records/excel")
@RequiredArgsConstructor
public class AttendanceExcelController {

	private final AttendanceExcelService attendanceExcelService;

	@RoleAuthority(value = MemberRole.MANAGER)
	@Operation(summary = "세션별 출석 기록 엑셀 다운로드 API")
	@GetMapping(params = "generationId")
	public ModelAndView downloadAttendanceRecordsAsExcelByGeneration(
		@RequestParam(name = "generationId") Long generationId) {
		return new ModelAndView(new ExcelView(),
			attendanceExcelService.getAttendanceRecordsExcelDataByGeneration(generationId));
	}

	@RoleAuthority(value = MemberRole.MANAGER)
	@Operation(summary = "출석별 출결 기록 엑셀 다운로드 API")
	@GetMapping(params = "attendanceIds")
	public ModelAndView downloadAttendanceRecordsAsExcelByAttendanceIds(
		@RequestParam(name = "attendanceIds") List<Long> attendanceIds) {
		return new ModelAndView(new ExcelView(),
			attendanceExcelService.getAttendanceRecordsExcelDataByAttendanceIds(attendanceIds));
	}
}
