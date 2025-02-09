package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.admin.dto.ApplyMemberInfoResponse;
import org.cotato.csquiz.api.admin.dto.MemberApproveRequest;
import org.cotato.csquiz.api.admin.dto.MemberEnrollInfoResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.RefusedMember;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.RefusedMemberRepository;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final RefusedMemberRepository refusedMemberRepository;
    private final MemberService memberService;
    private final EmailNotificationService emailNotificationService;
    private final GenerationReader generationReader;
    private final MemberReader memberReader;

    public List<ApplyMemberInfoResponse> getMembers(final MemberStatus status) {
        return memberRepository.findAllByStatus(status).stream()
                .map(member -> ApplyMemberInfoResponse.from(member, memberService.findBackFourNumber(member)))
                .toList();
    }

    @Transactional
    public void approveApplicant(final Long memberId, final MemberPosition position, final Long generationId) {
        Member member = memberReader.findById(memberId);
        if (member.getStatus() != MemberStatus.REJECTED && member.getStatus() != MemberStatus.REQUESTED) {
            throw new AppException(ErrorCode.CANNOT_ACTIVE);
        }

        Generation generation = generationReader.findById(generationId);
        member.approveMember();
        member.updatePassedGenerationNumber(generation.getNumber());
        member.updatePosition(position);
        memberRepository.save(member);

        // Todo: Event로 대체
        emailNotificationService.sendSignUpApprovedToEmail(member);
    }

    @Transactional
    public void reapproveApplicant(MemberApproveRequest request) {
        Member member = memberReader.findById(request.memberId());
        checkMemberStatus(member, MemberStatus.REJECTED);

        Generation generation = generationReader.findById(request.generationId());
        member.approveMember();

        member.updatePassedGenerationNumber(generation.getNumber());
        member.updatePosition(request.position());
        deleteRefusedMember(member);

        emailNotificationService.sendSignUpApprovedToEmail(member);
    }

    @Transactional
    public void rejectApplicant(final Long memberId) {
        Member member = memberReader.findById(memberId);
        checkMemberStatus(member, MemberStatus.REQUESTED);
        member.updateStatus(MemberStatus.REJECTED);
        memberRepository.save(member);
        addRefusedMember(member);

        emailNotificationService.sendSignupRejectionToEmail(member);
    }

    private void checkMemberStatus(final Member member, final MemberStatus status) {
        if (member.getStatus() != status) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
    }

    public List<MemberEnrollInfoResponse> findCurrentActiveMembers() {
        List<MemberRole> roles = MemberRoleGroup.ACTIVE_MEMBERS.getRoles();
        List<Member> activeMembers = memberRepository.findAllByRoleInQuery(roles);
        // Todo: 활동 부원 조회는 GenerationMember 기준으로 해야함 issue link : https://youthing.atlassian.net/jira/software/projects/COT/boards/2?selectedIssue=COT-139&sprints=8
        return activeMembers.stream()
                .map(MemberEnrollInfoResponse::of)
                .toList();
    }

    @Transactional
    public void updateMemberRole(final Long memberId, final MemberRole role) {
        Member member = memberReader.findById(memberId);
        if (!MemberRoleGroup.hasRole(MemberRoleGroup.ACTIVE_MEMBERS, member.getRole())) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
        member.updateRole(role);
        memberRepository.save(member);
    }

    @Transactional
    public void updateToRetireMembers(final List<Long> memberIds) {
        List<Member> members = memberReader.findAllByIdsInWithValidation(memberIds);

        if (members.stream().anyMatch(member -> member.getStatus() != MemberStatus.APPROVED)){
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
        members.forEach(member -> member.updateStatus(MemberStatus.RETIRED));
        memberRepository.saveAll(members);
        // Todo: OM으로 전환된 부원에게 이메일 발송 Event로 대체
    }

    public List<MemberEnrollInfoResponse> findOldMembers() {
        return memberRepository.findAllByStatus(MemberStatus.RETIRED).stream()
                .map(MemberEnrollInfoResponse::of)
                .toList();
    }

    @Transactional
    public void updateToApprovedMember(final Long memberId) {
        Member member = memberReader.findById(memberId);
        checkMemberStatus(member, MemberStatus.RETIRED);

        member.approveMember();
        memberRepository.save(member);
    }

    private void addRefusedMember(Member member) {
        RefusedMember refusedMember = RefusedMember.builder()
                .member(member)
                .build();
        refusedMemberRepository.save(refusedMember);
    }

    private void deleteRefusedMember(Member member) {
        RefusedMember refusedMember = refusedMemberRepository.findByMember(member)
                .orElseThrow(() -> new EntityNotFoundException("삭제하려는 멤버를 찾을 수 없습니다."));
        refusedMemberRepository.delete(refusedMember);
    }
}
