package org.cotato.csquiz.api.admin.controller;

import org.cotato.csquiz.api.admin.dto.ApplyMemberInfoResponse;
import org.cotato.csquiz.api.admin.dto.MemberApproveRequest;
import org.cotato.csquiz.api.admin.dto.MemberEnrollInfoResponse;
import org.cotato.csquiz.api.admin.dto.MemberRejectRequest;
import org.cotato.csquiz.api.admin.dto.UpdateActiveMemberRoleRequest;
import org.cotato.csquiz.api.admin.dto.UpdateActiveMemberToOldMemberRequest;
import org.cotato.csquiz.api.admin.dto.UpdateOldMemberRoleRequest;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/applicants")
    public ResponseEntity<List<ApplyMemberInfoResponse>> findApplicantList() {
        return ResponseEntity.ok().body(adminService.getMembers(MemberStatus.REQUESTED));
    }

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/reject-applicants")
    public ResponseEntity<List<ApplyMemberInfoResponse>> findRejectApplicantList() {
        return ResponseEntity.ok().body(adminService.getMembers(MemberStatus.REJECTED));
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/approve")
    public ResponseEntity<Void> approveApplicant(@RequestBody @Valid MemberApproveRequest request) {
        adminService.approveApplicant(request);
        return ResponseEntity.noContent().build();
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectApplicant(@RequestBody @Valid MemberRejectRequest request) {
        log.info("[가입자 거절 컨트롤러, 요청된 member id : {}]", request.memberId());
        adminService.rejectApplicant(request);
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/reapprove")
    public ResponseEntity<Void> reapproveApplicant(@RequestBody @Valid MemberApproveRequest request) {
        log.info("[가입자 재승인 컨트롤러, 요청된 member id : {}]", request.memberId());
        adminService.reapproveApplicant(request);
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/active-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> findCurrentActiveMembers() {
        return ResponseEntity.ok().body(adminService.findCurrentActiveMembers());
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/active-members/role")
    public ResponseEntity<Void> updateActiveMemberRole(
            @RequestBody @Valid UpdateActiveMemberRoleRequest request) {
        adminService.updateActiveMemberRole(request);
        return ResponseEntity.noContent().build();
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/active-members/to-old-members")
    public ResponseEntity<Void> updateActiveMembersToOldMembers(
            @RequestBody @Valid UpdateActiveMemberToOldMemberRequest request) {
        log.info("[현재 활동 중인 부원들을 OM으로 업데이트 하는 컨트롤러, 대상 member ids : {}]",
                request.memberIds());
        adminService.updateActiveMembersToOldMembers(request);
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/old-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> findOldMembers() {
        return ResponseEntity.ok().body(adminService.findOldMembers());
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/old-members/role")
    public ResponseEntity<Void> updateOldMemberToActiveGeneration(
            @RequestBody @Valid UpdateOldMemberRoleRequest request) {
        log.info("[OM을 현재 활동 기수로 업데이트하는 컨트롤러, 대상 member id: {}]", request.memberId());
        adminService.updateOldMemberToActiveGeneration(request);
        return ResponseEntity.noContent().build();
    }
}
