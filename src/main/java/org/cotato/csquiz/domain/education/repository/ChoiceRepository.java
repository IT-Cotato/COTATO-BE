package org.cotato.csquiz.domain.education.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findAllByMultipleQuiz(MultipleQuiz multipleQuiz);

    Optional<Choice> findByMultipleQuizAndChoiceNumber(MultipleQuiz multipleQuiz, int choiceNumber);

    @Transactional
    @Modifying
    @Query("delete from Choice c where c.multipleQuiz.id in :quizIds")
    void deleteAllByQuizIdsInQuery(@Param("quizIds") List<Long> quizIds);

    List<Choice> findAllByMultipleQuizId(Long quizId);

    List<Choice> findAllByMultipleQuizIdIn(List<Long> quizIds);
}
