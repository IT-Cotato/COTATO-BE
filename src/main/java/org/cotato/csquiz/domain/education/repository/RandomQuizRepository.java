package org.cotato.csquiz.domain.education.repository;

import org.cotato.csquiz.domain.education.entity.RandomQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomQuizRepository extends JpaRepository<RandomQuiz, Long> {
}
