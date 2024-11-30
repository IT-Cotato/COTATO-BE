package org.cotato.csquiz.domain.auth.service.component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
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

    public GenerationMember findById(Long id) {
        return generationMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("기수별 활동 부원이 존재하지 않습니다"));
    }
}
