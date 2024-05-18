package cotato.csquiz.service;

import static cotato.csquiz.domain.enums.MemberRole.GENERAL;
import static cotato.csquiz.domain.enums.MemberRole.MEMBER;
import static cotato.csquiz.domain.enums.MemberRole.OLD_MEMBER;
import static cotato.csquiz.domain.enums.MemberRole.REFUSED;

import cotato.csquiz.controller.dto.auth.ApplyMemberInfoResponse;
import cotato.csquiz.controller.dto.member.MemberApproveRequest;
import cotato.csquiz.controller.dto.member.MemberEnrollInfoResponse;
import cotato.csquiz.controller.dto.member.MemberRejectRequest;
import cotato.csquiz.controller.dto.member.UpdateActiveMemberRoleRequest;
import cotato.csquiz.controller.dto.member.UpdateActiveMemberToOldMemberRequest;
import cotato.csquiz.controller.dto.member.UpdateOldMemberRoleRequest;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.RefusedMember;
import cotato.csquiz.domain.enums.MemberRole;
import cotato.csquiz.domain.enums.MemberRoleGroup;
import cotato.csquiz.exception.AppException;
import cotato.csquiz.exception.ErrorCode;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.RefusedMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    public List<ApplyMemberInfoResponse> findApplicantList() {
        return createApplyInfoList(memberRepository.findAllByRole(GENERAL));
    }

    public List<ApplyMemberInfoResponse> findRejectApplicantList() {
        return createApplyInfoList(memberRepository.findAllByRole(REFUSED));
    }

    @Transactional
    public void approveApplicant(MemberApproveRequest request) {
        Member member = findMember(request.memberId());
        checkMemberRoleIsGeneral(member);

        if (member.getRole() == GENERAL) {
            Generation findGeneration = findGeneration(request.generationId());
            member.updateRole(MEMBER);
            member.updateGeneration(findGeneration.getNumber());
            member.updatePosition(request.position());
            memberRepository.save(member);
        }
    }

    @Transactional
    public void reapproveApplicant(MemberApproveRequest request) {
        Member member = findMember(request.memberId());

        if (member.getRole() == REFUSED) {
            Generation findGeneration = findGeneration(request.generationId());
            member.updateRole(MEMBER);
            member.updateGeneration(findGeneration.getNumber());
            member.updatePosition(request.position());
            deleteRefusedMember(member);
        }
    }

    @Transactional
    public void rejectApplicant(MemberRejectRequest request) {
        Member member = findMember(request.memberId());
        checkMemberRoleIsGeneral(member);
        if (member.getRole() == GENERAL) {
            member.updateRole(REFUSED);
            memberRepository.save(member);
            addRefusedMember(member);
        }
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
    }

    private void checkMemberRoleIsGeneral(Member member) {
        if (member.getRole() != GENERAL) {
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
            member.updateRole(OLD_MEMBER);
            memberRepository.save(member);
        }
    }

    public List<MemberEnrollInfoResponse> findOldMembers() {
        List<Member> oldMembers = memberRepository.findAllByRole(OLD_MEMBER);
        return oldMembers.stream()
                .map(MemberEnrollInfoResponse::of)
                .toList();
    }

    @Transactional
    public void updateOldMemberToActiveGeneration(UpdateOldMemberRoleRequest request) {
        Member member = findMember(request.memberId());
        checkMemberRoleIsOldMember(member);
        if (member.getRole() == OLD_MEMBER) {
            member.updateRole(MEMBER);
            memberRepository.save(member);
        }
    }

    private void checkMemberRoleIsOldMember(Member member) {
        if (member.getRole() != OLD_MEMBER) {
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
