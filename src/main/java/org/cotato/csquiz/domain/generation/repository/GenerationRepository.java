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

    @Query("SELECT g.id, g.number FROM Generation g WHERE g.id IN :generationId")
    Integer findGenerationNumberByGenerationId(@Param("generationId") Long generationId);

    @Query("SELECT g from Generation g where g.id in :generationIds")
    List<Generation> findAllByIdsInQuery(@Param("generationIds") List<Long> generationIds);
}
