package org.cotato.csquiz.migration.generation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.migration.MigrationJob;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CurrentGenerationMemberMigration implements MigrationJob {

    private static final int CURRENT_GENERATION = 10;

    private final MemberService memberService;
    private final GenerationMemberRepository generationMemberRepository;
    private final GenerationRepository generationRepository;

    @Override
    @Transactional
    public void migrate() {
        Generation generation = generationRepository.findByNumber(CURRENT_GENERATION)
                .orElseThrow(() -> new IllegalStateException("해당 기수가 존재하지 않습니다."));

        List<GenerationMember> currentGenerationMembers = memberService.findActiveMember().stream()
                .map(member -> GenerationMember.migrate(generation, member))
                .toList();

        generationMemberRepository.saveAll(currentGenerationMembers);
    }
}
