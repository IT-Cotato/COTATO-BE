package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.poi.AttendanceExcelSheetType;
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
    @GetMapping
    public ModelAndView downloadAttendanceRecordsAsExcelBySessions(
            @RequestParam(name = "type") AttendanceExcelSheetType sheetType,
            @RequestParam(name = "attendanceIds", required = false) List<Long> attendanceIds,
            @RequestParam(name = "generationId", required = false) Long generationId) {

        return switch (sheetType) {
            case EACH -> new ModelAndView(new ExcelView(),
                    attendanceExcelService.getAttendanceRecordsExcelDataBySessions(attendanceIds));
            case GENERATION -> new ModelAndView(new ExcelView(),
                    attendanceExcelService.getAttendanceRecordsExcelDataByGeneration(generationId));
            default -> throw new AppException(ErrorCode.NO_SUCH_TYPE);
        };
    }
}
