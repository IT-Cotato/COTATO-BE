package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.cotato.csquiz.api.member.dto.MemberInfo;
import org.cotato.csquiz.api.member.dto.MemberInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberMyPageInfoResponse;
import org.cotato.csquiz.api.member.dto.MemberResponse;
import org.cotato.csquiz.api.member.dto.ProfileInfoResponse;
import org.cotato.csquiz.api.member.dto.ProfileLinkRequest;
import org.cotato.csquiz.api.member.dto.SearchedMembersResponse;
import org.cotato.csquiz.api.policy.dto.CheckPolicyRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.s3.S3Uploader;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.MemberLeavingRequest;
import org.cotato.csquiz.domain.auth.entity.MemberPolicy;
import org.cotato.csquiz.domain.auth.entity.Policy;
import org.cotato.csquiz.domain.auth.entity.ProfileLink;
import org.cotato.csquiz.domain.auth.enums.ImageUpdateStatus;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.enums.PolicyCategory;
import org.cotato.csquiz.domain.auth.repository.MemberLeavingRequestRepository;
import org.cotato.csquiz.domain.auth.repository.MemberPolicyRepository;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.ProfileLinkRepository;
import org.cotato.csquiz.domain.auth.service.component.MemberLeavingRequestReader;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.auth.service.component.PolicyReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    @Value("${profile-image.default-url}")
    private String defaultProfileImageUrl;
    @Value("${profile-image.default-file}")
    private String defaultProfileImageFile;
    @Value("${profile-image.default-folder}")
    private String defaultProfileImageFolder;

    private static final String PROFILE_BUCKET_DIRECTORY = "profile";
    private final MemberReader memberReader;
    private final GenerationReader generationReader;
    private final PolicyReader policyReader;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EncryptService encryptService;
    private final S3Uploader s3Uploader;
    private final ProfileLinkRepository profileLinkRepository;
    private final GenerationMemberRepository generationMemberRepository;
    private final MemberLeavingRequestRepository memberLeavingRequestRepository;
    private final MemberLeavingRequestReader memberLeavingRequestReader;
    private final MemberPolicyRepository memberPolicyRepository;

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

    public ProfileInfoResponse findMemberProfileInfo(final Long memberId) {
        Member member = memberReader.findById(memberId);
        List<ProfileLink> profileLinks = profileLinkRepository.findAllByMember(member);
        return ProfileInfoResponse.of(member, profileLinks, defaultProfileImageUrl);
    }

    @Transactional
    public void updateMemberProfileInfo(final Member member, final String introduction, final String university,
                                        final List<ProfileLinkRequest> profileLinkRequests,
                                        ImageUpdateStatus imageUpdateStatus, final MultipartFile profileImage)
            throws ImageException {
        if (introduction != null) {
            member.updateIntroduction(introduction);
        }

        if (university != null) {
            member.updateUniversity(university);
        }

        if (profileLinkRequests != null) {
            profileLinkRepository.deleteAllByMember(member);
            List<ProfileLink> profileLinks = profileLinkRequests.stream()
                    .map(lr -> ProfileLink.of(member, lr.urlType(), lr.url()))
                    .toList();
            profileLinkRepository.saveAll(profileLinks);
        }

        updateProfileImage(member, profileImage, imageUpdateStatus);

        memberRepository.save(member);
    }

    private void deleteProfileImage(final Member member) {
        if (member.getProfileImage() != null && !isDefaultImage(member.getProfileImage())) {
            s3Uploader.deleteFile(member.getProfileImage());
        }
        member.updateProfileImage(null);
    }

    private boolean isDefaultImage(S3Info profileImage) {
        return profileImage.getFolderName().equals(defaultProfileImageFolder) &&
                profileImage.getFileName().equals(defaultProfileImageFile) &&
                profileImage.getUrl().equals(defaultProfileImageUrl);
    }

    private void updateProfileImage(final Member member, final MultipartFile profileImage,
                                    final ImageUpdateStatus imageUpdateStatus) throws ImageException {
        switch (imageUpdateStatus) {
            case KEEP:
                //프로필 이미지를 변경하지 않음.
                break;
            case UPDATE:
                if (profileImage == null || profileImage.isEmpty()) {
                    throw new AppException(ErrorCode.PROFILE_IMAGE_NOT_EXIST);
                }
                deleteProfileImage(member);
                member.updateProfileImage(s3Uploader.uploadFiles(profileImage, PROFILE_BUCKET_DIRECTORY));
                break;
            case DEFAULT:
                deleteProfileImage(member);
                S3Info defaultImage = S3Info.builder()
                        .folderName(defaultProfileImageFolder)
                        .fileName(defaultProfileImageFile)
                        .url(defaultProfileImageUrl)
                        .build();
                member.updateProfileImage(defaultImage);
                break;
            default:
                throw new IllegalArgumentException("잘못 된 ImageUpdateStatus 값" + imageUpdateStatus);
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

    public SearchedMembersResponse findAddableMembers(final Long generationId, Integer generationNumber,
                                                      MemberPosition memberPosition, String name) {
        Generation generation = generationReader.findById(generationId);
        List<Long> existMemberIds = generationMemberRepository.findAllByGenerationIdWithMember(generation.getId())
                .stream()
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
        return SearchedMembersResponse.from(filteredAddableMember);
    }

    @Transactional
    public void deactivateMember(final Member member, final String email, final String password,
                                 final List<CheckPolicyRequest> checkPolicyRequests) {
        validateMember(member, email, password);

        List<Policy> leavingPolicies = policyReader.getPoliciesByCategory(PolicyCategory.LEAVING);
        if (!isCheckedAllLeavingPolicies(checkPolicyRequests, leavingPolicies)) {
            throw new AppException(ErrorCode.NOT_CHECKED_ALL_LEAVING_POLICIES);
        }

        List<MemberPolicy> memberPolicies = leavingPolicies.stream()
                .map(policy -> MemberPolicy.of(true, member, policy.getId()))
                .toList();

        memberPolicyRepository.saveAll(memberPolicies);

        MemberLeavingRequest leavingRequest = MemberLeavingRequest.of(member, LocalDateTime.now());
        memberLeavingRequestRepository.save(leavingRequest);

        member.deactivate();
        memberRepository.save(member);
    }

    private boolean isCheckedAllLeavingPolicies(List<CheckPolicyRequest> policyIds, List<Policy> leavingPolicies) {
        Set<Long> leavingPolicyIds = leavingPolicies.stream().map(Policy::getId)
                .collect(Collectors.toUnmodifiableSet());
        Set<Long> checkedPolicyIds = policyIds.stream().map(CheckPolicyRequest::policyId)
                .collect(Collectors.toUnmodifiableSet());
        return SetUtils.isEqualSet(leavingPolicyIds, checkedPolicyIds);
    }

    private void validateMember(Member member, String email, String password) {
        if (!member.getEmail().equals(email)) {
            throw new AppException(ErrorCode.INVALID_EMAIL);
        }
        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
    }

    public Page<MemberResponse> getMembersByStatus(final MemberStatus status, Pageable pageable) {
        switch (status) {
            case APPROVED, RETIRED -> {
                Sort sort = Sort.by(
                        Sort.Order.desc("passedGenerationNumber"),
                        Sort.Order.asc("name")
                );
                Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
                return memberRepository.findAllByStatus(status, pageRequest)
                        .map(member -> MemberResponse.of(member, findBackFourNumber(member)));
            }
            default -> {
                return memberRepository.findAllByStatus(status, pageable)
                        .map(member -> MemberResponse.of(member, findBackFourNumber(member)));
            }
        }
    }

    @Transactional
    public void activateMember(final Long memberId) {
        Member member = memberReader.findById(memberId);

        if (member.getStatus() != MemberStatus.INACTIVE) {
            throw new AppException(ErrorCode.CANNOT_ACTIVE);
        }

        MemberLeavingRequest leavingRequest = memberLeavingRequestReader.getLeavingRequestByMember(member);

        member.updateStatus(MemberStatus.APPROVED);
        leavingRequest.updateIsReactivated(true);
        // Todo: event를 통한 이메일 발송
    }

    public Page<MemberResponse> getMembersByName(Integer passedGenerationNumber, MemberPosition position, String name,
                                                 MemberStatus memberStatus, Pageable pageable) {
        return memberRepository.findAllWithFiltersPageable(passedGenerationNumber, position, memberStatus, name,
                pageable).map(member -> MemberResponse.of(member, findBackFourNumber(member)));
    }
}
