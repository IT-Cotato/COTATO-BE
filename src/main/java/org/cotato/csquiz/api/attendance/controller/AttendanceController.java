package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.domain.attendance.service.AttendanceAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/attendance")
public class AttendanceController {

    private final AttendanceAdminService attendanceAdminService;

    @Operation(summary = "출석 정보 변경 API")
    @PatchMapping()
    public ResponseEntity<Void> updateAttendance(@RequestBody @Valid UpdateAttendanceRequest request) {
        attendanceAdminService.updateAttendance(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 출결사항 조회 API")
    @GetMapping("/records")
    public ResponseEntity<List<AttendanceRecordResponse>> findAttendanceRecords(
            @RequestParam(name = "generationId") Long generationId,
            @RequestParam(name = "month", required = false) @Min(1) @Max(12) Integer month
    ) {
        return ResponseEntity.ok().body(attendanceAdminService.findAttendanceRecords(generationId, month));
    }
}
