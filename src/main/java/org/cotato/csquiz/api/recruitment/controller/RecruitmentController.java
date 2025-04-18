package org.cotato.csquiz.api.recruitment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
import org.cotato.csquiz.api.recruitment.dto.RequestRecruitmentNotificationRequest;
import org.cotato.csquiz.domain.recruitment.service.RecruitmentInformationService;
import org.cotato.csquiz.domain.recruitment.service.RecruitmentNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "모집 정보", description = "모집 관련 API 입니다.")
@RequestMapping("/v2/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentInformationService recruitmentInformationService;
    private final RecruitmentNotificationService recruitmentNotificationService;

    @GetMapping
    @Operation(summary = "모집 정보 반환 API")
    public ResponseEntity<RecruitmentInfoResponse> findRecruitmentInfo() {
        return ResponseEntity.ok().body(recruitmentInformationService.findRecruitmentInfo());
    }

    @PostMapping("/notification")
    @Operation(summary = "모집 알림 신청 API")
    public ResponseEntity<Void> requestRecruitmentNotification(
            @RequestBody @Valid RequestRecruitmentNotificationRequest request) {
        recruitmentNotificationService.requestRecruitmentNotification(request.email(), request.policyCheck());
        return ResponseEntity.noContent().build();
    }
}
