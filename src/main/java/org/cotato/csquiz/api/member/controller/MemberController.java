package org.cotato.csquiz.api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.UpdatePasswordRequest;
import org.cotato.csquiz.api.member.dto.UpdatePhoneNumberRequest;
import org.cotato.csquiz.api.member.dto.UpdateProfileImageRequest;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> findMemberInfo(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok().body(memberService.findMemberInfo(memberId));
    }

    @PatchMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal Long memberId,
                                               @RequestBody @Valid UpdatePasswordRequest request) {
        memberService.updatePassword(memberId, request.password());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 전화번호 수정 API")
    @PatchMapping("/phone-number")
    public ResponseEntity<Void> updatePhoneNumber(@AuthenticationPrincipal Long memberId,
                                                  @RequestBody @Valid UpdatePhoneNumberRequest request) {
        memberService.updatePhoneNumber(memberId, request.phoneNumber());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 프로필 사진 수정 API")
    @PatchMapping(value = "/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal Long memberId,
            @ModelAttribute @Valid UpdateProfileImageRequest request) throws ImageException {
        memberService.updateMemberProfileImage(memberId, request.image());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 프로필 사진 삭제 API")
    @DeleteMapping("/profile-image")
    public ResponseEntity<Void> deleteProfileImage(
            @AuthenticationPrincipal Long memberId) {
        memberService.deleteMemberProfileImage(memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}/mypage")
    public ResponseEntity<MemberMyPageInfoResponse> findMyPageInfo(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok().body(memberService.findMyPageInfo(memberId));
    }
}
