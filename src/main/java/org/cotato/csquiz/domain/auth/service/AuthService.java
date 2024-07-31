package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.auth.dto.FindPasswordResponse;
import org.cotato.csquiz.api.auth.dto.JoinRequest;
import org.cotato.csquiz.api.auth.dto.LogoutRequest;
import org.cotato.csquiz.api.auth.dto.ReissueResponse;
import org.cotato.csquiz.api.auth.dto.SendEmailRequest;
import org.cotato.csquiz.api.member.dto.MemberEmailResponse;
import org.cotato.csquiz.common.config.jwt.BlackListRepository;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.common.config.jwt.RefreshToken;
import org.cotato.csquiz.common.config.jwt.RefreshTokenRepository;
import org.cotato.csquiz.common.config.jwt.Token;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.enums.EmailType;
import org.cotato.csquiz.domain.auth.constant.EmailConstants;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private static final String EMAIL_DELIMITER = "@";
    private static final int EXPOSED_LENGTH = 4;
    private static final String REFRESH_TOKEN = "refreshToken";

    private final MemberRepository memberRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final EmailVerificationService emailVerificationService;
    private final EncryptService encryptService;

    @Value("${jwt.refresh.expiration}")
    private int refreshTokenAge;

    @Transactional
    public void createLoginInfo(JoinRequest request) {
        if (!emailVerificationService.isSucceedEmail(EmailType.SIGNUP, request.email())) {
            throw new AppException(ErrorCode.UNVERIFIED_EMAIL);
        }
        validateService.checkDuplicateEmail(request.email());
        validateService.checkPasswordPattern(request.password());
        validateService.checkPhoneNumber(request.phoneNumber());

        String encryptedPhoneNumber = encryptService.encryptPhoneNumber(request.phoneNumber());
        validateService.checkDuplicatePhoneNumber(encryptedPhoneNumber);

        log.info("[회원 가입 서비스]: {}, {}", request.email(), request.name());

        Member newMember = Member.builder()
                .email(request.email())
                .password(bCryptPasswordEncoder.encode(request.password()))
                .name(request.name())
                .phoneNumber(encryptedPhoneNumber)
                .build();
        memberRepository.save(newMember);
    }

    @Transactional
    public ReissueResponse reissue(String refreshToken, HttpServletResponse response) {
        if (jwtTokenProvider.isExpired(refreshToken) || blackListRepository.existsById(refreshToken)) {
            log.warn("블랙리스트에 존재하는 토큰: {}", blackListRepository.existsById(refreshToken));
            throw new AppException(ErrorCode.REISSUE_FAIL);
        }
        Long memberId = jwtTokenProvider.getMemberId(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken);

        RefreshToken findToken = refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        log.info("[브라우저에서 들어온 쿠키] == [DB에 저장된 토큰], {}", refreshToken.equals(findToken.getRefreshToken()));

        if (!refreshToken.equals(findToken.getRefreshToken())) {
            log.warn("[쿠키로 들어온 토큰과 DB의 토큰이 일치하지 않음.]");
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }
        jwtTokenProvider.setBlackList(refreshToken);
        Token token = jwtTokenProvider.createToken(memberId, role);
        findToken.updateRefreshToken(token.getRefreshToken());
        refreshTokenRepository.save(findToken);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN, token.getRefreshToken());
        refreshCookie.setMaxAge(refreshTokenAge / 1000);
        log.info("[리프레시 쿠키 발급, 발급시간 : {}]", refreshTokenAge / 1000);
        refreshCookie.setPath("/");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        response.addCookie(refreshCookie);
        return ReissueResponse.from(token.getAccessToken());
    }

    @Transactional
    public void logout(LogoutRequest request, String refreshToken, HttpServletResponse response) {
        Long memberId = jwtTokenProvider.getMemberId(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST));
        jwtTokenProvider.setBlackList(refreshToken);
        log.info("[로그아웃 된 리프레시 토큰 블랙리스트 처리]");
        refreshTokenRepository.delete(existRefreshToken);
        Cookie deleteCookie = new Cookie(REFRESH_TOKEN, null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        deleteCookie.setSecure(true);
        deleteCookie.setHttpOnly(true);
        response.addCookie(deleteCookie);
        jwtTokenProvider.setBlackList(request.accessToken());
        log.info("[로그아웃 된 액세스 토큰 블랙리스트 처리]");
    }

    public void sendSignUpEmail(SendEmailRequest request) {
        validateService.emailNotExist(request.email());
        emailVerificationService.sendVerificationCodeToEmail(EmailType.SIGNUP, request.email(), EmailConstants.SIGNUP_SUBJECT);
    }

    public void verifySingUpCode(String email, String code) {
        emailVerificationService.verifyCode(EmailType.SIGNUP, email, code);
    }

    public void sendFindPasswordEmail(SendEmailRequest request) {
        validateService.emailExist(request.email());
        emailVerificationService.sendVerificationCodeToEmail(EmailType.UPDATE_PASSWORD, request.email(), EmailConstants.PASSWORD_SUBJECT);
    }

    public FindPasswordResponse verifyPasswordCode(String email, String code) {
        emailVerificationService.verifyCode(EmailType.UPDATE_PASSWORD, email, code);
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        String role = findMember.getRole().getKey();

        Token token = jwtTokenProvider.createToken(findMember.getId(), role);
        return FindPasswordResponse.from(token.getAccessToken());
    }

    public MemberEmailResponse findMemberEmail(String name, String phoneNumber) {
        String encryptedPhoneNumber = encryptService.encryptPhoneNumber(phoneNumber);
        Member findMember = memberRepository.findByPhoneNumber(encryptedPhoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("해당 전화번호를 가진 회원을 찾을 수 없습니다."));
        validateMatchName(findMember.getName(), name);
        String maskedId = getMaskId(findMember.getEmail());
        return MemberEmailResponse.from(maskedId);
    }

    private String getMaskId(String email) {
        String originId = email.split(EMAIL_DELIMITER)[0];
        String maskedPart = "*".repeat(EXPOSED_LENGTH);
        return originId.substring(0, 4) + maskedPart + EMAIL_DELIMITER + email.split(EMAIL_DELIMITER)[1];
    }

    private void validateMatchName(String originName, String requestName) {
        if (!originName.equals(requestName)) {
            throw new EntityNotFoundException("해당 이름을 가진 회원을 찾을 수 없습니다.");
        }
    }
}
