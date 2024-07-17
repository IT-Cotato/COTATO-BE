package org.cotato.csquiz.domain.auth.repository;

import java.util.List;
import org.cotato.csquiz.domain.auth.entity.MemberPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPolicyRepository extends JpaRepository<MemberPolicy, Long> {
    List<MemberPolicy> findAllByMemberId(Long memberId);
}
