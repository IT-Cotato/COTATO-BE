package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Record;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findAllByQuizAndReply(Quiz quiz, String answer);

    @Query("select o from Record o join fetch o.quiz")
    List<Record> findAllFetchJoin();

    Optional<Record> findByQuizAndMemberIdAndIsCorrect(Quiz findQuiz, Long memberId, boolean isCorrect);

    @Transactional
    @Modifying
    @Query("select r from Record r where r.quiz.id in :quizIds")
    List<Record> findAllByQuizIdsInQuery(@Param("quizIds") List<Long> quizIds);
}
