package org.cotato.csquiz.api.admin.controller;

import org.cotato.csquiz.api.admin.dto.ApplyMemberInfoResponse;
import org.cotato.csquiz.api.admin.dto.MemberApproveRequest;
import org.cotato.csquiz.api.admin.dto.MemberEnrollInfoResponse;
import org.cotato.csquiz.api.admin.dto.MemberRejectRequest;
import org.cotato.csquiz.api.admin.dto.UpdateMemberRoleRequest;
import org.cotato.csquiz.api.admin.dto.UpdateActiveMemberToOldMemberRequest;
import org.cotato.csquiz.api.admin.dto.UpdateOldMemberRoleRequest;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.service.AdminMemberService;
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

    private final AdminMemberService adminMemberService;

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/applicants")
    public ResponseEntity<List<ApplyMemberInfoResponse>> findApplicantList() {
        return ResponseEntity.ok().body(adminMemberService.getMembers(MemberStatus.REQUESTED));
    }

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/reject-applicants")
    public ResponseEntity<List<ApplyMemberInfoResponse>> findRejectApplicantList() {
        return ResponseEntity.ok().body(adminMemberService.getMembers(MemberStatus.REJECTED));
    }

    @Deprecated(since = "신입 감자 가입 승인 개발 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/approve")
    public ResponseEntity<Void> approveApplicant(@RequestBody @Valid MemberApproveRequest request) {
        adminMemberService.approveApplicant(request.memberId(), request.position(), request.generationId());
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "신입 감자 가입 승인 개발 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/reject")
    public ResponseEntity<Void> rejectApplicant(@RequestBody @Valid MemberRejectRequest request) {
        adminMemberService.rejectApplicant(request.memberId());
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "신입 감자 가입 승인 개발 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/reapprove")
    public ResponseEntity<Void> reapproveApplicant(@RequestBody @Valid MemberApproveRequest request) {
        adminMemberService.reapproveApplicant(request);
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/active-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> findCurrentActiveMembers() {
        return ResponseEntity.ok().body(adminMemberService.findCurrentActiveMembers());
    }

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/active-members/role")
    public ResponseEntity<Void> updateActiveMemberRole(
            @RequestBody @Valid UpdateMemberRoleRequest request) {
        adminMemberService.updateMemberRole(request.memberId(), request.role());
        return ResponseEntity.noContent().build();
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/active-members/to-old-members")
    public ResponseEntity<Void> updateActiveMembersToOldMembers(
            @RequestBody @Valid UpdateActiveMemberToOldMemberRequest request) {
        adminMemberService.updateToRetireMembers(request.memberIds());
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since = "부원 접근 권한 및 신입 부원 승인 피쳐 이후")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping("/old-members")
    public ResponseEntity<List<MemberEnrollInfoResponse>> findOldMembers() {
        return ResponseEntity.ok().body(adminMemberService.findOldMembers());
    }

    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/old-members/role")
    public ResponseEntity<Void> updateOldMemberToActiveGeneration(
            @RequestBody @Valid UpdateOldMemberRoleRequest request) {
        adminMemberService.updateOldMemberToActiveGeneration(request);
        return ResponseEntity.noContent().build();
    }
}
