package org.cotato.csquiz.domain.generation.repository;

import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerationMemberRepository extends JpaRepository<GenerationMember, Long> {
}
