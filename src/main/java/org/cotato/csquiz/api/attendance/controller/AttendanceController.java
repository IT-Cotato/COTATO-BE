package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.AttendanceTimeResponse;
import org.cotato.csquiz.api.attendance.dto.AttendancesResponse;
import org.cotato.csquiz.api.attendance.dto.MemberAttendanceRecordsResponse;
import org.cotato.csquiz.api.attendance.dto.OfflineAttendanceRequest;
import org.cotato.csquiz.api.attendance.dto.OnlineAttendanceRequest;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.domain.attendance.service.AttendanceAdminService;
import org.cotato.csquiz.domain.attendance.service.AttendanceRecordService;
import org.cotato.csquiz.domain.attendance.service.AttendanceService;
import org.cotato.csquiz.domain.attendance.util.AttendanceExcelHeaderUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    private final AttendanceAdminService attendanceAdminService;
    private final AttendanceService attendanceService;
    private final AttendanceRecordService attendanceRecordService;

    @Operation(summary = "출석 정보 변경 API")
    @PatchMapping
    public ResponseEntity<Void> updateAttendance(@RequestBody @Valid UpdateAttendanceRequest request) {
        attendanceAdminService.updateAttendanceByAttendanceId(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "세션 시간 반환 API")
    @GetMapping("/info")
    public ResponseEntity<AttendanceTimeResponse> findAttendanceTimeInfo(@RequestParam("sessionId") Long sessionId) {
        return ResponseEntity.status(HttpStatus.OK).body(attendanceService.getAttendanceDetailInfo(sessionId));
    }

    @Operation(summary = "회원 출결사항 기간 단위 조회 API")
    @GetMapping("/records")
    public ResponseEntity<List<AttendanceRecordResponse>> findAttendanceRecords(
            @RequestParam(name = "generationId") Long generationId,
            @RequestParam(name = "month", required = false) @Min(value = 1, message = "달은 1 이상이어야 합니다.") @Max(value = 12, message = "달은 12 이하이어야 합니다") Integer month
    ) {
        return ResponseEntity.ok().body(attendanceAdminService.findAttendanceRecords(generationId, month));
    }

    @Operation(summary = "회원 출결사항 출석 단위 조회 API")
    @GetMapping("/{attendance-id}/records")
    public ResponseEntity<List<AttendanceRecordResponse>> findAttendanceRecordsByAttendance(
            @PathVariable("attendance-id") Long attendanceId) {
        return ResponseEntity.ok().body(attendanceAdminService.findAttendanceRecordsByAttendance(attendanceId));
    }

    @Operation(summary = "기수별 출석 목록 조회 API")
    @GetMapping
    public ResponseEntity<AttendancesResponse> findAttendancesByGeneration(
            @RequestParam("generationId") Long generationId) {
        return ResponseEntity.ok().body(attendanceService.findAttendancesByGenerationId(generationId));
    }

    @Operation(summary = "대면 출결 입력 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이미 출석을 완료함"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "출석 시간이 아님"
                    )
            }
    )
    @PostMapping(value = "/records/offline")
    public ResponseEntity<AttendResponse> submitOfflineAttendanceRecord(
            @RequestBody @Valid OfflineAttendanceRequest request,
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok().body(attendanceRecordService.submitRecord(request, memberId));
    }

    @Operation(summary = "비대면 출결 입력 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "이미 출석을 완료함"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "출석 시간이 아님"
                    )
            })
    @PostMapping(value = "/records/online")
    public ResponseEntity<AttendResponse> submitOnlineAttendanceRecord(
            @RequestBody @Valid OnlineAttendanceRequest request,
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok().body(attendanceRecordService.submitRecord(request, memberId));
    }

    @Operation(summary = "부원의 기수별 출결 기록 반환 API")
    @GetMapping("/records/members")
    public ResponseEntity<MemberAttendanceRecordsResponse> findAllRecordsByGeneration(
            @RequestParam("generationId") Long generationId,
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok().body(attendanceRecordService.findAllRecordsBy(generationId, memberId));
    }

    @Operation(summary = "세션별 출석 기록 엑셀 다운로드 API")
    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadAttendanceRecordsAsExcelBySessions(
            @RequestParam(name = "attendanceIds") List<Long> attendanceIds) {

        byte[] excelFile = attendanceAdminService.createExcelForSessionAttendance(attendanceIds);
        String finalFileName = attendanceAdminService.getEncodedFileName(attendanceIds);

        HttpHeaders headers = AttendanceExcelHeaderUtil.createExcelDownloadHeaders(finalFileName);

        return ResponseEntity.ok().headers(headers).body(excelFile);
    }
}
