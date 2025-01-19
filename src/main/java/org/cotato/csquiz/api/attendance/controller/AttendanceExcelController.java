package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.poi.ExcelView;
import org.cotato.csquiz.domain.attendance.service.AttendanceExcelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v2/api/attendances/records/excel")
@RequiredArgsConstructor
public class AttendanceExcelController {

    private final AttendanceExcelService attendanceExcelService;

    @Operation(summary = "세션별 출석 기록 엑셀 다운로드 API")
    @GetMapping(params = "generationId")
    public ModelAndView downloadAttendanceRecordsAsExcelBySessions(@RequestParam(name = "generationId") Long generationId) {
        return new ModelAndView(new ExcelView(), attendanceExcelService.getAttendanceRecordsExcelDataByGeneration(generationId));
    }
}