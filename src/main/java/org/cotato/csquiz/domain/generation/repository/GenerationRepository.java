package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    Optional<Generation> findByNumber(int number);

    List<Generation> findByNumberGreaterThanEqual(int generationNumber);

    @Query("SELECT g from Generation g where g.id in :generationIds")
    List<Generation> findAllByIdsInQuery(@Param("generationIds") List<Long> generationIds);

    @Query("SELECT g FROM Generation g WHERE :currentDate BETWEEN g.period.startDate AND g.period.endDate")
    Optional<Generation> findByCurrentDate(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT g FROM Generation g WHERE g.period.endDate < :currentDate ORDER BY g.period.endDate DESC LIMIT 1")
    Optional<Generation> findPreviousGenerationByCurrentDate(@Param("currentDate") LocalDate currentDate);
}
