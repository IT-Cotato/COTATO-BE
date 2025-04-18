package org.cotato.csquiz.domain.auth.repository;

import java.util.List;
import java.util.Optional;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByPhoneNumber(String phone);

    List<Member> findAllByRole(MemberRole memberRole);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("select m from Member m where m.role in :roles")
    List<Member> findAllByRoleInQuery(@Param("roles") List<MemberRole> roles);

    List<Member> findAllByStatus(MemberStatus memberStatus);

    Page<Member> findAllByStatus(MemberStatus memberStatus, Pageable pageable);
}
