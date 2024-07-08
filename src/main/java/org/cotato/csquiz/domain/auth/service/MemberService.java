package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberInfo;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.UpdatePhoneNumberRequest;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EncryptService encryptService;
    private final ValidateService validateService;

    public MemberInfoResponse findMemberInfo(Long id) {
        Member findMember = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원을 찾을 수 없습니다."));

        String rawBackFourNumber = findBackFourNumber(findMember);
        log.info("이름 + 번호 4자리: {}({})", findMember.getName(), rawBackFourNumber);
        return MemberInfoResponse.from(findMember, rawBackFourNumber);
    }

    public String findBackFourNumber(Member member) {
        String decryptedPhone = member.getPhoneNumber();
        String originPhoneNumber = encryptService.decryptPhoneNumber(decryptedPhone);
        int numberLength = originPhoneNumber.length();
        return originPhoneNumber.substring(numberLength - 4);
    }

    @Transactional
    public void updatePassword(String accessToken, String password) {
        Long memberId = jwtTokenProvider.getMemberId(accessToken);
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        validateService.checkPasswordPattern(password);
        validateIsSameBefore(findMember.getPassword(), password);

        findMember.updatePassword(bCryptPasswordEncoder.encode(password));
    }

    private void validateIsSameBefore(String originPassword, String newPassword) {
        if (bCryptPasswordEncoder.matches(newPassword, originPassword)) {
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }
    }

    @Transactional
    public void updatePhoneNumber(String accessToken, String phoneNumber) {
        Long memberId = jwtTokenProvider.getMemberId(accessToken);
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        String encryptedPhoneNumber = encryptService.encryptPhoneNumber(phoneNumber);
        findMember.updatePhoneNumber(encryptedPhoneNumber);
    }

    public MemberMyPageInfoResponse findMyPageInfo(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        String originPhoneNumber = encryptService.decryptPhoneNumber(findMember.getPhoneNumber());
        return MemberMyPageInfoResponse.of(findMember, originPhoneNumber);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
    }

    public MemberInfo getMemberInfo(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
        return MemberInfo.of(findMember, findBackFourNumber(findMember));
    }
}
