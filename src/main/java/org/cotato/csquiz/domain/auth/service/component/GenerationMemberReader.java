package org.cotato.csquiz.domain.auth.service.component;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerationMemberReader {

    private final GenerationMemberRepository generationMemberRepository;

    public boolean isExist(Generation generation, Member member) {
        return generationMemberRepository.existsByGenerationAndMember(generation, member);
    }
}
