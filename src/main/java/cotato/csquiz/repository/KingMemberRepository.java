package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.KingMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KingMemberRepository extends JpaRepository<KingMember, Long> {
    List<KingMember> findAllByEducation(Education education);
}
