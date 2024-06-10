package org.cotato.csquiz.api.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.UpdatePasswordRequest;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> findMemberInfo(
            @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);
        Long memberId = jwtTokenProvider.getMemberId(accessToken);
        return ResponseEntity.ok().body(memberService.findMemberInfo(memberId));
    }

    @PatchMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody @Valid UpdatePasswordRequest request) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);
        memberService.updatePassword(accessToken, request.password());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}/mypage")
    public ResponseEntity<MemberMyPageInfoResponse> findMyPageInfo(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok().body(memberService.findMyPageInfo(memberId));
    }
}
