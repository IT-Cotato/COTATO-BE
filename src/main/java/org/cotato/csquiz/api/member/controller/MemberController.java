package org.cotato.csquiz.api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.io.IOException;
import javax.naming.NoPermissionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberApproveRequest;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.admin.dto.UpdateActiveMemberToOldMemberRequest;
import org.cotato.csquiz.api.admin.dto.UpdateMemberRoleRequest;
import org.cotato.csquiz.api.member.dto.AddableMembersResponse;
import org.cotato.csquiz.api.member.dto.DeactivateRequest;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberResponse;
import org.cotato.csquiz.api.member.dto.ProfileInfoResponse;
import org.cotato.csquiz.api.member.dto.UpdatePasswordRequest;
import org.cotato.csquiz.api.member.dto.UpdatePhoneNumberRequest;
import org.cotato.csquiz.api.member.dto.UpdateProfileInfoRequest;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.service.AdminMemberService;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/member")
public class MemberController {

    private final MemberService memberService;
    private final AdminMemberService adminMemberService;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> findMemberInfo(
            @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok().body(memberService.findMemberInfo(member));
    }

    @Operation(summary = "기수별 멤버에 추가 가능한 멤버 반환 API")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping
    public ResponseEntity<AddableMembersResponse> findAddableMembersForGenerationMember(
            @RequestParam(name = "generationId") @Parameter(description = "추가하고 싶은 기수의 Id") Long generationId,
            @RequestParam(name = "passedGenerationNumber", required = false) @Parameter(description = "멤버 합격 기수") Integer generationNumber,
            @RequestParam(name = "position", required = false) @Parameter(description = "멤버 포지션") MemberPosition position,
            @RequestParam(name = "name", required = false) @Parameter(description = "멤버 이름") String name
    ) {
        return ResponseEntity.ok()
                .body(memberService.findAddableMembers(generationId, generationNumber, position, name));
    }

    @PatchMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal Member member,
                                               @RequestBody @Valid UpdatePasswordRequest request) {
        memberService.updatePassword(member, request.password());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 전화번호 수정 API")
    @PatchMapping("/phone-number")
    public ResponseEntity<Void> updatePhoneNumber(@AuthenticationPrincipal Member member,
                                                  @RequestBody @Valid UpdatePhoneNumberRequest request) {
        memberService.updatePhoneNumber(member, request.phoneNumber());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 프로필 정보 수정 API")
    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfileInfo(@AuthenticationPrincipal Member member,
                                                  @RequestPart final UpdateProfileInfoRequest request,
                                                  @RequestPart(required = false) MultipartFile profileImage)
            throws IOException {
        memberService.updateMemberProfileInfo(member, request.introduction(), request.university(),
                request.profileLinks(), profileImage);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 프로필 정보 반환 API")
    @GetMapping("/{memberId}/profile")
    public ResponseEntity<ProfileInfoResponse> findProfileInfo(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(memberService.findMemberProfileInfo(memberId));
    }

    @GetMapping("/{memberId}/mypage")
    public ResponseEntity<MemberMyPageInfoResponse> findMyPageInfo(@AuthenticationPrincipal Member member, @PathVariable("memberId") Long memberId)
            throws NoPermissionException {
        if (!member.getId().equals(memberId)) {
            throw new NoPermissionException("본인 외의 정보는 조회할 수 없습니다.");
        }
        return ResponseEntity.ok().body(memberService.findMyPageInfo(memberId));
    }

    @Operation(summary = "회원 비활성화 요청 API")
    @PostMapping("/{memberId}/deactivate")
    public ResponseEntity<Void> deactivateMember(@PathVariable("memberId") Long memberId,
                                                 @Valid @RequestBody DeactivateRequest request,
                                                 @AuthenticationPrincipal Member member) throws NoPermissionException {
        if (!member.getId().equals(memberId)) {
            throw new NoPermissionException("본인 외의 회원을 비활성화할 수 없습니다.");
        }
        memberService.deactivateMember(member, request.email(), request.password(), request.checkedPolicies());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 상태에 따른 조회 요청 API")
    @RoleAuthority(MemberRole.ADMIN)
    @GetMapping(params = "status")
    public ResponseEntity<Page<MemberResponse>> findMembersByStatus(@RequestParam("status") MemberStatus status, Pageable pageable) {
        return ResponseEntity.ok().body(memberService.getMembersByStatus(status, pageable));
    }

    @Operation(summary = "부원 가입 승인")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/{memberId}/approve")
    public ResponseEntity<Void> approveApplicant(@PathVariable("memberId") final Long memberId,
                                                 @RequestBody @Valid MemberApproveRequest request) {
        adminMemberService.approveApplicant(memberId, request.position(), request.generationId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "부원 가입 거절")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/{memberId}/reject")
    public ResponseEntity<Void> rejectApplicant(@PathVariable("memberId") final Long memberId) {
        adminMemberService.rejectApplicant(memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "부원 역할 변경")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/{memberId}/role")
    public ResponseEntity<Void> updateMemberRole(@PathVariable("memberId") final Long memberId,
                                                 @RequestBody @Valid UpdateMemberRoleRequest request) {
        adminMemberService.updateMemberRole(memberId, request.role());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 활성화 API")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping("/{memberId}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable("memberId") Long memberId) {
        memberService.activateMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "부원 OM 전환")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping(value = "/status", params = "target=RETIRE")
    public ResponseEntity<Void> updateMembersToOldMembers(@RequestBody UpdateActiveMemberToOldMemberRequest request) {
        adminMemberService.updateToRetireMembers(request.memberIds());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "OM을 일반 부원으로 전환")
    @RoleAuthority(MemberRole.ADMIN)
    @PatchMapping(value = "/{memberId}/status", params = "target=APPROVED")
    public ResponseEntity<Void> updateToApprovedMember(@PathVariable("memberId") Long memberId) {
        adminMemberService.updateToApprovedMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
