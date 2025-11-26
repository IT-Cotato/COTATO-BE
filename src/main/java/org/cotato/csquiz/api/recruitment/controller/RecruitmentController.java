package org.cotato.csquiz.api.recruitment.controller;

import org.cotato.csquiz.api.recruitment.dto.ChangeRecruitmentInfoRequest;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentNotificationLogsResponse;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentNotificationPendingResponse;
import org.cotato.csquiz.api.recruitment.dto.RequestNotificationRequest;
import org.cotato.csquiz.api.recruitment.dto.RequestRecruitmentNotificationRequest;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.recruitment.service.RecruitmentInformationService;
import org.cotato.csquiz.domain.recruitment.service.RecruitmentNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

	@Operation(summary = "모집 정보 수정 API")
	@RoleAuthority(MemberRole.ADMIN)
	@PutMapping
	public ResponseEntity<Void> changeRecruitmentInfo(@RequestBody @Valid ChangeRecruitmentInfoRequest request) {
		recruitmentInformationService.changeRecruitmentInfo(request.isOpened(), request.startDate(), request.endDate(),
			request.recruitmentUrl());
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "모집 알람 대기자 수 반환 API")
	@RoleAuthority(MemberRole.ADMIN)
	@GetMapping("/notifications/pending")
	public ResponseEntity<RecruitmentNotificationPendingResponse> countPendingNotification() {
		return ResponseEntity.ok().body(recruitmentNotificationService.countPendingNotification());
	}

	@PostMapping("/notification")
	@Operation(summary = "모집 알림 신청 API")
	public ResponseEntity<Void> requestRecruitmentNotification(
		@RequestBody @Valid RequestRecruitmentNotificationRequest request) {
		recruitmentNotificationService.requestRecruitmentNotification(request.email(), request.policyCheck());
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "모집 알림 신청 결과 확인 API")
	@RoleAuthority(MemberRole.ADMIN)
	@GetMapping("/notifications/logs")
	public ResponseEntity<RecruitmentNotificationLogsResponse> findNotificationLogs() {
		return ResponseEntity.ok().body(recruitmentNotificationService.findNotificationLogs());
	}

	@Operation(summary = "모집 알림 전송 API")
	@RoleAuthority(MemberRole.ADMIN)
	@PostMapping("/notification/requester")
	public ResponseEntity<Void> requestRecruitmentNotification(@RequestBody @Valid RequestNotificationRequest request,
		@AuthenticationPrincipal Member member) {
		recruitmentNotificationService.sendRecruitmentNotificationMail(request.generationNumber(), member);
		return ResponseEntity.noContent().build();
	}
}
