package org.cotato.csquiz.domain.auth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.event.CotatoEventPublisher;
import org.cotato.csquiz.domain.auth.event.EmailSendEvent;
import org.cotato.csquiz.domain.auth.event.EmailSendEventDto;
import org.cotato.csquiz.common.event.EventType;
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

    private final CotatoEventPublisher eventPublisher;
    private final MemberRepository memberRepository;
    private final RefusedMemberRepository refusedMemberRepository;
    private final GenerationReader generationReader;
    private final MemberReader memberReader;

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

        EmailSendEventDto dto = EmailSendEventDto.builder().member(member).build();
        eventPublisher.publishEvent(EmailSendEvent.builder()
                .type(EventType.APPROVE_MEMBER)
                .data(dto)
                .build());
    }

    @Transactional
    public void rejectApplicant(final Long memberId) {
        Member member = memberReader.findById(memberId);
        checkMemberStatus(member, MemberStatus.REQUESTED);
        member.updateStatus(MemberStatus.REJECTED);
        memberRepository.save(member);
        addRefusedMember(member);

        EmailSendEventDto dto = EmailSendEventDto.builder().member(member).build();
        eventPublisher.publishEvent(EmailSendEvent.builder()
                .type(EventType.REJECT_MEMBER)
                .data(dto)
                .build());
    }

    private void checkMemberStatus(final Member member, final MemberStatus status) {
        if (member.getStatus() != status) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
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

        if (members.stream().anyMatch(member -> member.getStatus() != MemberStatus.APPROVED)) {
            throw new AppException(ErrorCode.ROLE_IS_NOT_MATCH);
        }
        if (members.stream().anyMatch(member -> member.getRole() == MemberRole.DEV)) {
            throw new AppException(ErrorCode.CANNOT_CHANGE_DEV_ROLE);
        }
        members.forEach(member -> member.updateStatus(MemberStatus.RETIRED));
        memberRepository.saveAll(members);
        // Todo: OM으로 전환된 부원에게 이메일 발송 Event로 대체
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
}
