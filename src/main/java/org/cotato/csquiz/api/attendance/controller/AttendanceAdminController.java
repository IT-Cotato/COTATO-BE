package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.api.education.dto.UpdateEducationRequest;
import org.cotato.csquiz.domain.attendance.service.AttendanceAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/attendance/admin")
public class AttendanceAdminController {

    private final AttendanceAdminService attendanceAdminService;

    @Operation(summary = "출석 정보 변경 API")
    @PatchMapping("/update")
    public ResponseEntity<Void> updateAttendance(@RequestBody @Valid UpdateAttendanceRequest request) {
        attendanceAdminService.updateAttendance(request);
        return ResponseEntity.noContent().build();
    }
}
