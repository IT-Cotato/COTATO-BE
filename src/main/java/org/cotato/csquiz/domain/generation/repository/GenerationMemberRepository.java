package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerationMemberRepository extends JpaRepository<GenerationMember, Long> {
    List<GenerationMember> findAllByGeneration(Generation generation);
}
