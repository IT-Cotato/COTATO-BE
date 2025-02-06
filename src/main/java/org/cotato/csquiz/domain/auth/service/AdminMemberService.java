package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.admin.dto.ApplyMemberInfoResponse;
import org.cotato.csquiz.api.admin.dto.MemberApproveRequest;
import org.cotato.csquiz.api.admin.dto.MemberEnrollInfoResponse;
import org.cotato.csquiz.api.admin.dto.MemberRejectRequest;
import org.cotato.csquiz.api.admin.dto.UpdateActiveMemberRoleRequest;
import org.cotato.csquiz.api.admin.dto.UpdateActiveMemberToOldMemberRequest;
import org.cotato.csquiz.api.admin.dto.UpdateOldMemberRoleRequest;
import org.cotato.csquiz.domain.auth.entity.RefusedMember;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberRoleGroup;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.RefusedMemberRepository;
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
    public void approveApplicant(MemberApproveRequest request) {
        Member member = memberReader.findById(request.memberId());
        checkMemberStatus(member, MemberStatus.REQUESTED);

        Generation generation = generationReader.findById(request.generationId());
        member.approveMember();
        member.updateGeneration(generation.getNumber());
        member.updatePosition(request.position());
        memberRepository.save(member);

        emailNotificationService.sendSignUpApprovedToEmail(member);
    }

    @Transactional
    public void reapproveApplicant(MemberApproveRequest request) {
        Member member = memberReader.findById(request.memberId());
        checkMemberStatus(member, MemberStatus.REJECTED);

        Generation generation = generationReader.findById(request.generationId());
        member.approveMember();

        member.updateGeneration(generation.getNumber());
        member.updatePosition(request.position());
        deleteRefusedMember(member);

        emailNotificationService.sendSignUpApprovedToEmail(member);
    }

    @Transactional
    public void rejectApplicant(MemberRejectRequest request) {
        Member member = memberReader.findById(request.memberId());
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
    public void updateActiveMemberRole(UpdateActiveMemberRoleRequest request) {
        Member member = memberReader.findById(request.memberId());
        if (!MemberRoleGroup.hasRole(MemberRoleGroup.ACTIVE_MEMBERS, member.getRole())) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
        member.updateRole(request.role());
        memberRepository.save(member);
    }

    @Transactional
    public void updateActiveMembersToOldMembers(UpdateActiveMemberToOldMemberRequest request) {
        for (Long memberId : request.memberIds()) {
            Member member = memberReader.findById(memberId);

            if (!MemberRoleGroup.hasRole(MemberRoleGroup.ACTIVE_MEMBERS, member.getRole())) {
                throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
            }
            member.updateStatus(MemberStatus.RETIRED);
            memberRepository.save(member);

            emailNotificationService.sendOldMemberConversionToEmail(member);
        }
    }

    public List<MemberEnrollInfoResponse> findOldMembers() {
        return memberRepository.findAllByStatus(MemberStatus.RETIRED).stream()
                .map(MemberEnrollInfoResponse::of)
                .toList();
    }

    @Transactional
    public void updateOldMemberToActiveGeneration(UpdateOldMemberRoleRequest request) {
        Member member = memberReader.findById(request.memberId());
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
