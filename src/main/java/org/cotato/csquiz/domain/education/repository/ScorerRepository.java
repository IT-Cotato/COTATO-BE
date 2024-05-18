package org.cotato.csquiz.domain.education.repository;

import org.cotato.csquiz.domain.education.entity.Scorer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ScorerRepository extends JpaRepository<Scorer, Long> {
    Optional<Scorer> findByQuizId(Long quizId);

    List<Scorer> findAllByQuizId(Long quizId);

    @Transactional
    @Modifying
    @Query("select s from Scorer s where s.quizId in :quizIds")
    List<Scorer> findAllByQuizIdsInQuery(@Param("quizIds") List<Long> quizIds);
}
