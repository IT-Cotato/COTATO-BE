package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.attendance.dto.AttendResponse;
import org.cotato.csquiz.api.attendance.dto.MemberAttendanceRecordsResponse;
import org.cotato.csquiz.api.attendance.dto.OfflineAttendanceRequest;
import org.cotato.csquiz.api.attendance.dto.OnlineAttendanceRequest;
import org.cotato.csquiz.domain.attendance.service.AttendanceRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "출결 기록", description = "출결 기록 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/attendances/records")
public class AttendanceRecordController {

    private final AttendanceRecordService attendanceRecordService;


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
    @PostMapping(value = "/offline")
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
    @PostMapping(value = "/online")
    public ResponseEntity<AttendResponse> submitOnlineAttendanceRecord(
            @RequestBody @Valid OnlineAttendanceRequest request,
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok().body(attendanceRecordService.submitRecord(request, memberId));
    }

    @Operation(summary = "부원의 기수별 출결 기록 반환 API")
    @GetMapping("/members")
    public ResponseEntity<MemberAttendanceRecordsResponse> findAllRecordsByGeneration(
            @RequestParam("generationId") Long generationId,
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok().body(attendanceRecordService.findAllRecordsBy(generationId, memberId));
    }
}
