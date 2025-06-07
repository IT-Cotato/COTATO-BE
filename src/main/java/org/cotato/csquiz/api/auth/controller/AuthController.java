package org.cotato.csquiz.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.auth.dto.FindPasswordResponse;
import org.cotato.csquiz.api.auth.dto.JoinRequest;
import org.cotato.csquiz.api.auth.dto.JoinResponse;
import org.cotato.csquiz.api.auth.dto.LogoutRequest;
import org.cotato.csquiz.api.auth.dto.ReissueResponse;
import org.cotato.csquiz.api.auth.dto.SendEmailRequest;
import org.cotato.csquiz.api.member.dto.MemberEmailResponse;
import org.cotato.csquiz.domain.auth.service.AuthService;
import org.cotato.csquiz.domain.auth.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "인증 관련 API", description = "회원 인증 관련 API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원 가입 API")
    @PostMapping("/join")
    public ResponseEntity<JoinResponse> joinAuth(@RequestBody @Valid JoinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createMember(request));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> tokenReissue(@CookieValue(name = "refreshToken") String refreshToken,
                                                        HttpServletResponse response) {
        ReissueResponse reissueResponse = authService.reissue(refreshToken);

        response.setHeader("Authorization", "Bearer " + reissueResponse.accessToken());

        Cookie cookie = CookieUtil.createRefreshCookie(reissueResponse.refreshToken());
        response.addCookie(cookie);

        return ResponseEntity.ok().body(reissueResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken") String refreshToken,
                                       @RequestBody @Valid LogoutRequest request, HttpServletResponse response) {
        authService.logout(request, refreshToken, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verification", params = "type=sign-up")
    public ResponseEntity<Void> sendSignUpVerificationCode(@Valid @RequestBody SendEmailRequest request) {
        authService.sendSignUpEmail(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/verification", params = "type=sign-up")
    public ResponseEntity<Void> verifyCode(@RequestParam(name = "email") String email,
                                           @RequestParam(name = "code") String code) {
        authService.verifySingUpCode(email, code);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verification", params = "type=find-password")
    public ResponseEntity<Void> sendFindPasswordVerificationCode(@RequestBody @Valid SendEmailRequest request) {
        authService.sendFindPasswordEmail(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/verification", params = "type=find-password")
    public ResponseEntity<FindPasswordResponse> verifyPasswordCode(@RequestParam(name = "email") String email,
                                                                   @RequestParam(name = "code") String code) {
        return ResponseEntity.ok().body(authService.verifyPasswordCode(email, code));
    }

    @GetMapping("/email")
    public ResponseEntity<MemberEmailResponse> findEmail(@RequestParam(name = "name") String name,
                                                         @RequestParam("phone") String phoneNumber) {
        return ResponseEntity.ok(authService.findMemberEmail(name, phoneNumber));
    }
}
