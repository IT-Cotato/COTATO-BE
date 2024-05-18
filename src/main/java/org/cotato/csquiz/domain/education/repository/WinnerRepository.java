package org.cotato.csquiz.domain.education.repository;

import java.util.Optional;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Winner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
    Optional<Winner> findByEducation(Education education);
}
