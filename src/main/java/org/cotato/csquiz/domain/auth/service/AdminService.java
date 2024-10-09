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
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.RefusedMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final GenerationRepository generationRepository;
    private final RefusedMemberRepository refusedMemberRepository;
    private final MemberService memberService;
    private final EmailVerificationService emailVerificationService;

    public List<ApplyMemberInfoResponse> findApplicantList() {
        return createApplyInfoList(memberRepository.findAllByRole(MemberRole.GENERAL));
    }

    public List<ApplyMemberInfoResponse> findRejectApplicantList() {
        return createApplyInfoList(memberRepository.findAllByRole(MemberRole.REFUSED));
    }

    @Transactional
    public void approveApplicant(MemberApproveRequest request) {
        Member member = findMember(request.memberId());
        checkMemberRoleIsGeneral(member);

        if (member.getRole() == MemberRole.GENERAL) {
            Generation findGeneration = findGeneration(request.generationId());
            member.updateRole(MemberRole.MEMBER);
            member.updateGeneration(findGeneration.getNumber());
            member.updatePosition(request.position());
            memberRepository.save(member);
        }

        emailVerificationService.sendSignUpApprovedToEmail(member);
    }

    @Transactional
    public void reapproveApplicant(MemberApproveRequest request) {
        Member member = findMember(request.memberId());

        if (member.getRole() == MemberRole.REFUSED) {
            Generation findGeneration = findGeneration(request.generationId());
            member.updateRole(MemberRole.MEMBER);
            member.updateGeneration(findGeneration.getNumber());
            member.updatePosition(request.position());
            deleteRefusedMember(member);
        }
    }

    @Transactional
    public void rejectApplicant(MemberRejectRequest request) {
        Member member = findMember(request.memberId());
        checkMemberRoleIsGeneral(member);
        if (member.getRole() == MemberRole.GENERAL) {
            member.updateRole(MemberRole.REFUSED);
            memberRepository.save(member);
            addRefusedMember(member);
        }
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
    }

    private void checkMemberRoleIsGeneral(Member member) {
        if (member.getRole() != MemberRole.GENERAL) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
    }

    public List<MemberEnrollInfoResponse> findCurrentActiveMembers() {
        List<MemberRole> roles = MemberRoleGroup.ACTIVE_MEMBERS.getRoles();
        List<Member> activeMembers = memberRepository.findAllByRoleInQuery(roles);

        return activeMembers.stream()
                .map(MemberEnrollInfoResponse::of)
                .toList();
    }

    @Transactional
    public void updateActiveMemberRole(UpdateActiveMemberRoleRequest request) {
        Member member = findMember(request.memberId());
        if (!MemberRoleGroup.hasRole(MemberRoleGroup.ACTIVE_MEMBERS, member.getRole())) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
        member.updateRole(request.role());
        memberRepository.save(member);
    }

    @Transactional
    public void updateActiveMembersToOldMembers(UpdateActiveMemberToOldMemberRequest request) {
        for (Long memberId : request.memberIds()) {
            Member member = findMember(memberId);
            if (!MemberRoleGroup.hasRole(MemberRoleGroup.ACTIVE_MEMBERS, member.getRole())) {
                throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
            }
            member.updateRole(MemberRole.OLD_MEMBER);
            memberRepository.save(member);
        }
    }

    public List<MemberEnrollInfoResponse> findOldMembers() {
        List<Member> oldMembers = memberRepository.findAllByRole(MemberRole.OLD_MEMBER);
        return oldMembers.stream()
                .map(MemberEnrollInfoResponse::of)
                .toList();
    }

    @Transactional
    public void updateOldMemberToActiveGeneration(UpdateOldMemberRoleRequest request) {
        Member member = findMember(request.memberId());
        checkMemberRoleIsOldMember(member);
        if (member.getRole() == MemberRole.OLD_MEMBER) {
            member.updateRole(MemberRole.MEMBER);
            memberRepository.save(member);
        }
    }

    private void checkMemberRoleIsOldMember(Member member) {
        if (member.getRole() != MemberRole.OLD_MEMBER) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_OLD_MEMBER);
        }
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

    private Generation findGeneration(Long generationId) {
        return generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));
    }

    private List<ApplyMemberInfoResponse> createApplyInfoList(List<Member> applicantList) {
        return applicantList.stream()
                .map(member -> ApplyMemberInfoResponse.from(member, memberService.findBackFourNumber(member)))
                .toList();
    }
}
