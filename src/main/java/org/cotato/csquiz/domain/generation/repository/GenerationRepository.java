package org.cotato.csquiz.domain.generation.repository;

import java.util.Optional;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    Optional<Generation> findByNumber(int number);
}
