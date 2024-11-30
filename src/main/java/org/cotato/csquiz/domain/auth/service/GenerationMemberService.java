package org.cotato.csquiz.domain.auth.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.member.dto.CreateGenerationMember;
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

@Service
@RequiredArgsConstructor
public class GenerationMemberService {

    private final GenerationMemberRepository generationMemberRepository;
    private final GenerationMemberReader generationMemberReader;
    private final MemberReader memberReader;
    private final GenerationReader generationReader;

    @Transactional
    public void addGenerationMember(List<CreateGenerationMember> createGenerationMembers) {
        List<GenerationMember> newGenerationMembers = createGenerationMembers.stream()
                .map(this::buildGenerationMember)
                .toList();
        generationMemberRepository.saveAll(newGenerationMembers);
    }

    private GenerationMember buildGenerationMember(CreateGenerationMember createGenerationMember) {
        Member member = memberReader.findById(createGenerationMember.memberId());
        Generation generation = generationReader.findById(createGenerationMember.generationId());

        if (generationMemberReader.isExist(generation, member)) {
            throw new AppException(ErrorCode.GENERATION_MEMBER_EXIST);
        }
        return GenerationMember.of(generation, member);
    }

    @Transactional
    public void updateGenerationMemberRole(Long generationMemberId, MemberRole memberRole) {
        GenerationMember generationMember = generationMemberReader.findById(generationMemberId);
        generationMember.updateMemberRole(memberRole);
    }
}
