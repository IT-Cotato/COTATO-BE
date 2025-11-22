package org.cotato.csquiz.domain.education.repository;

import java.util.List;
import java.util.Optional;

import org.cotato.csquiz.domain.education.entity.ShortAnswer;
import org.cotato.csquiz.domain.education.entity.ShortQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ShortAnswerRepository extends JpaRepository<ShortAnswer, Long> {
	List<ShortAnswer> findAllByShortQuiz(ShortQuiz shortQuiz);

	Optional<ShortAnswer> findByShortQuizAndContent(ShortQuiz quiz, String answer);

	@Transactional
	@Modifying
	@Query("delete from ShortAnswer s where s.shortQuiz.id in :quizIds")
	void deleteAllByQuizIdsInQuery(@Param("quizIds") List<Long> quizIds);

	List<ShortAnswer> findAllByShortQuizIdIn(List<Long> shortQuizIds);
}
