package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Winner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
    Optional<Winner> findByEducation(Education education);
}
