package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceTimeResponse;
import org.cotato.csquiz.api.attendance.dto.AttendancesResponse;
import org.cotato.csquiz.api.attendance.dto.GenerationMemberAttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRecordRequest;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.attendance.service.AttendanceRecordService;
import org.cotato.csquiz.domain.attendance.service.AttendanceService;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "출석 정보", description = "출석 관련 API 입니다.")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v2/api/attendances")
public class AttendanceController {

    private final AttendanceRecordService attendanceRecordService;
    private final AttendanceService attendanceService;

    @Operation(summary = "출석 단건 조회")
    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> getAttendance(@PathVariable("attendanceId") Long attendanceId) {
        return ResponseEntity.ok().body(attendanceService.getAttendance(attendanceId));
    }

    @Operation(summary = "출석 정보 변경 API")
    @PatchMapping
    public ResponseEntity<Void> updateAttendance(@RequestBody @Valid UpdateAttendanceRequest request) {
        attendanceService.updateAttendance(request.attendanceId(), request.location(), request.attendTime().attendanceDeadLine(), request.attendTime().lateDeadLine());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "세션 시간 반환 API")
    @GetMapping("/info")
    public ResponseEntity<AttendanceTimeResponse> findAttendanceTimeInfo(@RequestParam("sessionId") Long sessionId) {
        return ResponseEntity.status(HttpStatus.OK).body(attendanceService.getAttendanceDetailInfo(sessionId));
    }

    @Operation(summary = "회원 출결사항 기수 단위 조회 API")
    @RoleAuthority(MemberRole.MANAGER)
    @GetMapping("/records")
    public ResponseEntity<List<GenerationMemberAttendanceRecordResponse>> findAttendanceRecords(
            @RequestParam(name = "generationId") Long generationId
    ) {
        return ResponseEntity.ok().body(attendanceRecordService.findAttendanceRecords(generationId));
    }

    @Operation(summary = "회원 출결사항 출석 단위 조회 API")
    @RoleAuthority(MemberRole.MANAGER)
    @GetMapping("/{attendance-id}/records")
    public ResponseEntity<List<AttendanceRecordResponse>> findAttendanceRecordsByAttendance(
            @PathVariable("attendance-id") Long attendanceId) {
        return ResponseEntity.ok().body(attendanceRecordService.findAttendanceRecordsByAttendance(attendanceId));
    }

    @Operation(summary = "회원 출결사항 수정 API")
    @RoleAuthority(MemberRole.MANAGER)
    @PatchMapping("/{attendance-id}/records")
    public ResponseEntity<Void> updateAttendanceRecords(
            @PathVariable("attendance-id") Long attendanceId,
            @RequestBody @Valid UpdateAttendanceRecordRequest request) {
        attendanceRecordService.updateAttendanceRecords(attendanceId, request.memberId(), request.result());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기수별 출석 목록 조회 API")
    @GetMapping
    public ResponseEntity<AttendancesResponse> findAttendancesByGeneration(
            @RequestParam("generationId") Long generationId) {
        return ResponseEntity.ok().body(attendanceService.findAttendancesByGenerationId(generationId));
    }
}
