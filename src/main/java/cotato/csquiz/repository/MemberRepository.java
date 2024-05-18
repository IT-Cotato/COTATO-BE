package cotato.csquiz.repository;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.enums.MemberRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phone);

    List<Member> findAllByRole(MemberRole memberRole);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("select m from Member m where m.role in :roles")
    List<Member> findAllByRoleInQuery(@Param("roles") List<MemberRole> roles);
}
