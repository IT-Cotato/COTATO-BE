package org.cotato.csquiz.api.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.auth.dto.FindPasswordResponse;
import org.cotato.csquiz.api.auth.dto.JoinRequest;
import org.cotato.csquiz.api.auth.dto.LogoutRequest;
import org.cotato.csquiz.api.auth.dto.ReissueResponse;
import org.cotato.csquiz.api.auth.dto.SendEmailRequest;
import org.cotato.csquiz.api.member.dto.MemberEmailResponse;
import org.cotato.csquiz.domain.auth.service.AuthService;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<Void> joinAuth(@RequestBody @Valid JoinRequest request) {
        log.info("[회원 가입 컨트롤러]: {}, {}", request.email(), request.name());
        authService.createLoginInfo(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> tokenReissue(@CookieValue(name = "refreshToken") String refreshToken,
                                                        HttpServletResponse response) {
        log.info("[액세스 토큰 재발급 컨트롤러]: 쿠키 존재 여부, {}", !refreshToken.isEmpty());
        return ResponseEntity.ok().body(authService.reissue(refreshToken, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken") String refreshToken,
                                       @RequestBody @Valid LogoutRequest request, HttpServletResponse response) {
        authService.logout(request, refreshToken, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verification", params = "type=sign-up")
    public ResponseEntity<Void> sendSignUpVerificationCode(@Valid @RequestBody SendEmailRequest request) {
        log.info("[회원 가입 시 이메일 인증 요청 컨트롤러]: 요청된 이메일 {}", request.email());
        authService.sendSignUpEmail(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/verification", params = "type=sign-up")
    public ResponseEntity<Void> verifyCode(@RequestParam(name = "email") String email,
                                           @RequestParam(name = "code") String code) {
        log.info("[회원 가입 시 인증 코드 확인 컨트롤러]: {}", email);
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
        log.info("아이디 찾기 컨트롤러: {}", name);
        return ResponseEntity.ok(authService.findMemberEmail(name, phoneNumber));
    }
}
