package cotato.csquiz.service;

import cotato.csquiz.config.jwt.JwtTokenProvider;
import cotato.csquiz.controller.dto.auth.MemberInfoResponse;
import cotato.csquiz.controller.dto.member.MemberInfo;
import cotato.csquiz.controller.dto.member.MemberMyPageInfoResponse;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void checkCorrectPassword(String accessToken, String password) {
        Long memberId = jwtTokenProvider.getMemberId(accessToken);
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        if (!bCryptPasswordEncoder.matches(password, findMember.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
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
