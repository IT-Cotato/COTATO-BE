package org.cotato.csquiz.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.service.component.GenerationMemberReader;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenerationMemberService {

    private final GenerationMemberRepository generationMemberRepository;
    private final GenerationMemberReader generationMemberReader;
    private final MemberReader memberReader;
    private final GenerationReader generationReader;

    @Transactional
    public void addGenerationMember(Long memberId, Long generationId) {
        Member member = memberReader.findById(memberId);
        Generation generation = generationReader.findById(generationId);

        if (generationMemberReader.isExist(generation, member)) {
            throw new AppException(ErrorCode.GENERATION_MEMBER_EXIST);
        }
        generationMemberRepository.save(GenerationMember.of(generation, member));
    }

    @Transactional
    public void updateGenerationMemberRole(Long generationMemberId, MemberRole memberRole) {
        GenerationMember generationMember = generationMemberReader.findById(generationMemberId);
        generationMember.updateMemberRole(memberRole);
    }
}
