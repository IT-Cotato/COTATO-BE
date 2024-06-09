package org.cotato.csquiz.domain.education.repository;

import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.enums.QuizStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Transactional
    @Modifying
    @Query("delete from Quiz p where p.id in :ids")
    void deleteAllByQuizIdsInQuery(@Param("ids") List<Long> ids);

    List<Quiz> findAllByEducationId(Long educationId);

    List<Quiz> findAllByStatus(QuizStatus status);

    Optional<Quiz> findByStatusAndEducationId(QuizStatus status, Long educationId);

    List<Quiz> findAllByStart(QuizStatus quizStatus);

    @Transactional
    @Modifying
    @Query("select q from Quiz q where q.education.id in :educationIds")
    List<Quiz> findAllByEducationIdsInQuery(@Param("educationIds") List<Long> educationIds);

    Optional<Quiz> findByEducationIdAndNumber(Long educationId, Integer i);

    Optional<Quiz> findFirstByEducationOrderByNumberDesc(Education education);
}
