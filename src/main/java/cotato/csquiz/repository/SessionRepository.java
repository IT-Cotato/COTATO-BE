package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.enums.CSEducation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByGeneration(Generation generation);

    List<Session> findAllByGenerationAndCsEducation(Generation generation, CSEducation csEducation);
}
