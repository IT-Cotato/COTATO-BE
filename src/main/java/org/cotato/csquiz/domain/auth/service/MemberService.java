package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberInfo;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.ProfileLinkRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.ProfileLink;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.auth.service.component.ProfileLinkWriter;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private static final String PROFILE_BUCKET_DIRECTORY = "profile";
    private final MemberReader memberReader;
    private final GenerationReader generationReader;
    private final ProfileLinkWriter profileLinkWriter;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EncryptService encryptService;
    private final ValidateService validateService;
    private final S3Uploader s3Uploader;

    public MemberInfoResponse findMemberInfo(final Member member) {
        String rawBackFourNumber = findBackFourNumber(member);
        log.info("이름 + 번호 4자리: {}({})", member.getName(), rawBackFourNumber);
        return MemberInfoResponse.from(member, rawBackFourNumber);
    }

    public String findBackFourNumber(Member member) {
        String decryptedPhone = member.getPhoneNumber();
        String originPhoneNumber = encryptService.decryptPhoneNumber(decryptedPhone);
        int numberLength = originPhoneNumber.length();
        return originPhoneNumber.substring(numberLength - 4);
    }

    @Transactional
    public void updatePassword(final Member member, final String password) {
        validateService.checkPasswordPattern(password);
        validateIsSameBefore(member.getPassword(), password);

        member.updatePassword(bCryptPasswordEncoder.encode(password));
        memberRepository.save(member);
    }

    private void validateIsSameBefore(String originPassword, String newPassword) {
        if (bCryptPasswordEncoder.matches(newPassword, originPassword)) {
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }
    }

    @Transactional
    public void updatePhoneNumber(final Member member, String phoneNumber) {
        String encryptedPhoneNumber = encryptService.encryptPhoneNumber(phoneNumber);
        member.updatePhoneNumber(encryptedPhoneNumber);
        memberRepository.save(member);
    }

    @Transactional
    public void updateMemberProfileInfo(final Long memberId, final String introduction, final String university,
                                        final List<ProfileLinkRequest> profileLinkRequests) {
        Member member = memberReader.findById(memberId);

        member.updateIntroduction(introduction);
        member.updateUniversity(university);
        profileLinkWriter.deleteAllByMember(member);

        List<ProfileLink> profileLinks = profileLinkRequests.stream()
                .map(lr -> ProfileLink.of(member, lr.linkType(), lr.link()))
                .toList();
        profileLinkWriter.createProfileLinks(profileLinks);
    }

    @Transactional
    public void updateMemberProfileImage(final Member member, MultipartFile image) throws ImageException {
        if (image.isEmpty()) {
            throw new AppException(ErrorCode.FILE_IS_EMPTY);
        }

        if (member.getProfileImage() != null) {
            s3Uploader.deleteFile(member.getProfileImage());
        }

        S3Info s3Info = s3Uploader.uploadFiles(image, PROFILE_BUCKET_DIRECTORY);
        member.updateProfileImage(s3Info);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMemberProfileImage(final Member member) {
        if (member.getProfileImage() != null) {
            s3Uploader.deleteFile(member.getProfileImage());
        }

        member.updateProfileImage(null);
        memberRepository.save(member);
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

    public List<Member> findActiveMember() {
        Generation currentGeneration = generationReader.findByDate(LocalDate.now());
        return memberReader.findAllGenerationMember(currentGeneration);
    }
}
