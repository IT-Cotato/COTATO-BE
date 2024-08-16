package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    Optional<Generation> findByNumber(int number);

    List<Generation> findByNumberGreaterThanEqual(int generationNumber);

    @Query("SELECT g.number FROM Generation g WHERE g.id = :generationId")
    Integer findGenerationNumberByGenerationId(@Param("generationId") Long generationId);
}
