package org.cotato.csquiz.domain.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.auth.entity.RefusedMember;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefusedMemberRepository extends JpaRepository<RefusedMember, Long> {
    Optional<RefusedMember> findByMember(Member member);

    List<RefusedMember> findAllByCreatedAtBefore(LocalDateTime localDateTime);
}
