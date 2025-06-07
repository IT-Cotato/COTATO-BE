package org.cotato.csquiz.domain.auth.service;

import static org.cotato.csquiz.common.util.EmailUtil.getVerificationMessageBody;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.auth.dto.FindPasswordResponse;
import org.cotato.csquiz.api.auth.dto.JoinRequest;
import org.cotato.csquiz.api.auth.dto.JoinResponse;
import org.cotato.csquiz.api.auth.dto.LogoutRequest;
import org.cotato.csquiz.api.auth.dto.ReissueResponse;
import org.cotato.csquiz.api.auth.dto.SendEmailRequest;
import org.cotato.csquiz.api.member.dto.MemberEmailResponse;
import org.cotato.csquiz.common.config.jwt.BlackListRepository;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.common.config.jwt.RefreshToken;
import org.cotato.csquiz.common.config.jwt.RefreshTokenRepository;
import org.cotato.csquiz.common.config.jwt.Token;
import org.cotato.csquiz.common.email.EmailSender;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.constant.EmailConstants;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.EmailType;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.service.component.EmailCodeManager;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String EMAIL_DELIMITER = "@";
    private static final int EXPOSED_LENGTH = 4;
    private static final String REFRESH_TOKEN = "refreshToken";

    private final PolicyService policyService;
    private final MemberReader memberReader;
    private final MemberRepository memberRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final EmailCodeManager emailCodeManager;
    private final EncryptService encryptService;
    private final EmailSender emailSender;

    @Transactional
    public JoinResponse createMember(final JoinRequest request) {
        validateService.checkDuplicateEmail(request.email());

        String encryptedPhoneNumber = encryptService.encryptPhoneNumber(request.phoneNumber());
        validateService.checkDuplicatePhoneNumber(encryptedPhoneNumber);
        log.info("[회원 가입 서비스]: {}, {}", request.email(), request.name());

        Member newMember = Member.defaultMember(request.email(), bCryptPasswordEncoder.encode(request.password()), request.name(), encryptedPhoneNumber);
        memberRepository.save(newMember);

        policyService.checkPolicies(newMember, request.policies());

        return JoinResponse.from(newMember);
    }

    public ReissueResponse reissue(final String refreshToken) {
        if (jwtTokenProvider.isExpired(refreshToken) || blackListRepository.existsById(refreshToken)) {
            log.warn("블랙리스트에 존재하는 토큰: {}", blackListRepository.existsById(refreshToken));
            throw new AppException(ErrorCode.REISSUE_FAIL);
        }

        Member member = jwtTokenProvider.getMember(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("해당 리프레시 토큰을 가진 회원을 찾을 수 없습니다."));

        RefreshToken findToken = refreshTokenRepository.findById(member.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST));
        log.info("[브라우저에서 들어온 쿠키] == [DB에 저장된 토큰], {}", refreshToken.equals(findToken.getRefreshToken()));

        if (!refreshToken.equals(findToken.getRefreshToken())) {
            log.warn("[쿠키로 들어온 토큰과 DB의 토큰이 일치하지 않음.]");
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST);
        }

        jwtTokenProvider.setBlackList(refreshToken);

        Token token = jwtTokenProvider.createToken(member);
        findToken.updateRefreshToken(token.getRefreshToken());
        refreshTokenRepository.save(findToken);

        return ReissueResponse.from(token);
    }

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

        String verificationCode = emailCodeManager.getRandomCode(EmailType.SIGNUP, request.email());
        String verificationMessage = getVerificationMessageBody(verificationCode);

        emailSender.sendEmail(request.email(), verificationMessage, EmailConstants.SIGNUP_SUBJECT);
    }

    public void verifySingUpCode(String email, String code) {
        emailCodeManager.verifyCode(EmailType.SIGNUP, email, code);
    }

    public void sendFindPasswordEmail(SendEmailRequest request) {
        validateService.emailExist(request.email());

        String verificationCode = emailCodeManager.getRandomCode(EmailType.UPDATE_PASSWORD, request.email());
        String verificationMessage = getVerificationMessageBody(verificationCode);

        emailSender.sendEmail(request.email(), verificationMessage, EmailConstants.SIGNUP_SUBJECT);
    }

    public FindPasswordResponse verifyPasswordCode(String email, String code) {
        emailCodeManager.verifyCode(EmailType.UPDATE_PASSWORD, email, code);
        Member member = memberReader.getByEmail(email);

        Token token = jwtTokenProvider.createToken(member);
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
