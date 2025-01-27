package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.admin.dto.MemberInfoResponse;
import org.cotato.csquiz.api.member.dto.AddableMembersResponse;
import org.cotato.csquiz.api.member.dto.MemberInfo;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.ProfileInfoResponse;
import org.cotato.csquiz.api.member.dto.ProfileLinkRequest;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.ProfileLink;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.ProfileLinkRepository;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
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
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EncryptService encryptService;
    private final ValidateService validateService;
    private final S3Uploader s3Uploader;
    private final ProfileLinkRepository profileLinkRepository;
    private final GenerationMemberRepository generationMemberRepository;

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

    public ProfileInfoResponse findMemberProfileInfo(final Member member) {
        List<ProfileLink> profileLinks = profileLinkRepository.findAllByMember(member);
        return ProfileInfoResponse.of(member, profileLinks);
    }

    @Transactional
    public void updateMemberProfileInfo(final Member member, final String introduction, final String university,
                                        final List<ProfileLinkRequest> profileLinkRequests, final MultipartFile profileImage)
            throws ImageException {
        member.updateIntroduction(introduction);
        member.updateUniversity(university);

        profileLinkRepository.deleteAllByMember(member);
        List<ProfileLink> profileLinks = profileLinkRequests.stream()
                .map(lr -> ProfileLink.of(member, lr.urlType(), lr.url()))
                .toList();
        profileLinkRepository.saveAll(profileLinks);

        deleteProfileImage(member);
        if (profileImage != null) {
            member.updateProfileImage(s3Uploader.uploadFiles(profileImage, PROFILE_BUCKET_DIRECTORY));
        }
        memberRepository.save(member);
    }

    private void deleteProfileImage(final Member member) {
        if (member.getProfileImage() != null) {
            s3Uploader.deleteFile(member.getProfileImage());
            member.updateProfileImage(null);
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

    public List<Member> findActiveMember() {
        Generation currentGeneration = generationReader.findByDate(LocalDate.now());
        return memberReader.findAllGenerationMember(currentGeneration);
    }

    public AddableMembersResponse findAddableMembers(final Long generationId, Integer generationNumber, MemberPosition memberPosition, String name) {
        List<Long> existMemberIds = generationMemberRepository.findAllByGenerationIdWithMember(generationId).stream()
                .map(gm -> gm.getMember().getId())
                .toList();

        List<Member> filteredAddableMember = memberRepository.findAllWithFilters(generationNumber, memberPosition, name)
                .stream()
                .filter(member -> member.isApproved() || member.isRetired())
                .filter(member -> !existMemberIds.contains(member.getId()))
                .sorted(Comparator
                        .comparing(Member::isApproved)
                        .reversed()
                        .thenComparing(Member::getName)
                )
                .toList();
        return AddableMembersResponse.from(filteredAddableMember);
    }
}
