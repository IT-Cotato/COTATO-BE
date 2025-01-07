package org.cotato.csquiz.api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import javax.naming.NoPermissionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.UpdatePasswordRequest;
import org.cotato.csquiz.api.member.dto.UpdatePhoneNumberRequest;
import org.cotato.csquiz.api.member.dto.UpdateProfileImageRequest;
import org.cotato.csquiz.api.member.dto.UpdateProfileInfoRequest;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> findMemberInfo(
            @AuthenticationPrincipal Member member) {
        return ResponseEntity.ok().body(memberService.findMemberInfo(member));
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
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfileInfo(@AuthenticationPrincipal Member member,
                                                  @RequestPart final UpdateProfileInfoRequest request,
                                                  @RequestPart(required = false) MultipartFile profileImage)
            throws IOException {
        memberService.updateMemberProfileInfo(member, request.introduction(), request.university(),
                request.profileLinks(), profileImage);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 프로필 사진 수정 API")
    @PatchMapping(value = "/profile-image", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal Member member,
            @ModelAttribute @Valid UpdateProfileImageRequest request) throws ImageException {
        memberService.updateMemberProfileImage(member, request.image());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버 프로필 사진 삭제 API")
    @DeleteMapping("/profile-image")
    public ResponseEntity<Void> deleteProfileImage(
            @AuthenticationPrincipal Member member) {
        memberService.deleteMemberProfileImage(member);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}/mypage")
    public ResponseEntity<MemberMyPageInfoResponse> findMyPageInfo(@AuthenticationPrincipal Member member, @PathVariable("memberId") Long memberId)
            throws NoPermissionException {
        if (!member.getId().equals(memberId)) {
            throw new NoPermissionException("본인 외의 정보는 조회할 수 없습니다.");
        }
        return ResponseEntity.ok().body(memberService.findMyPageInfo(memberId));
    }
}
