package org.cotato.csquiz.domain.auth.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.member.dto.GenerationMemberInfo;
import org.cotato.csquiz.api.member.dto.GenerationMemberInfoResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.component.GenerationMemberReader;
import org.cotato.csquiz.domain.auth.service.component.MemberReader;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.enums.GenerationMemberRole;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.cotato.csquiz.domain.generation.service.component.GenerationReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GenerationMemberService {

    private final GenerationMemberRepository generationMemberRepository;
    private final GenerationMemberReader generationMemberReader;
    private final MemberReader memberReader;
    private final GenerationReader generationReader;

    public GenerationMemberInfoResponse findGenerationMemberByGeneration(Long generationId) {
        Generation generation = generationReader.findById(generationId);
        List<GenerationMemberInfo> generationMemberInfos = generationMemberReader.findAllByGenerationWithMember(generation).stream()
                .sorted(Comparator.comparing(GenerationMember::getMemberName))
                .map(GenerationMemberInfo::from)
                .toList();
        return GenerationMemberInfoResponse.from(generationMemberInfos);
    }

    @Transactional
    public void addGenerationMember(Long generationId, List<Long> memberIds) {
        Generation generation = generationReader.findById(generationId);
        List<Member> members = memberReader.findAllByIdsInWithValidation(memberIds);
        if (generationMemberReader.existsByGenerationIdAndMemberIn(generation, members)) {
            throw new AppException(ErrorCode.GENERATION_MEMBER_EXIST);
        }

        List<GenerationMember> newGenerationMembers = members.stream()
                .map(member -> GenerationMember.of(generation, member))
                .toList();
        generationMemberRepository.saveAll(newGenerationMembers);
    }

    @Transactional
    public void updateGenerationMemberRole(final Long generationMemberId, final GenerationMemberRole memberRole) {
        GenerationMember generationMember = generationMemberReader.findById(generationMemberId);
        generationMember.updateMemberRole(memberRole);
    }

    @Transactional
    public void deleteGenerationMember(Long generationMemberId) {
        checkGenerationMembersExist(generationMemberId);
        generationMemberRepository.deleteById(generationMemberId);
    }

    private void checkGenerationMembersExist(Long generationMemberId) {
        if (!generationMemberRepository.existsById(generationMemberId)) {
            throw new EntityNotFoundException("멤버가 존재하지 않습니다");
        }
    }
}
