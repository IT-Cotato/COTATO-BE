package org.cotato.csquiz.domain.education.repository;

import java.util.List;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findAllByQuizAndReply(Quiz quiz, String answer);

    @Query("select o from Record o join fetch o.quiz")
    List<Record> findAllFetchJoin();

    @Transactional
    @Modifying
    @Query("select r from Record r where r.quiz.id in :quizIds")
    List<Record> findAllByQuizIdsInQuery(@Param("quizIds") List<Long> quizIds);

	boolean existsByQuizAndMemberIdAndIsCorrect(Quiz quiz, Long memberId, boolean isCorrect);
}
