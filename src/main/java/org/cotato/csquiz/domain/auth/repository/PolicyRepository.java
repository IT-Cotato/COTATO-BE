package org.cotato.csquiz.domain.auth.repository;

import java.util.List;
import org.cotato.csquiz.domain.auth.entity.Policy;
import org.cotato.csquiz.domain.auth.enums.PolicyCategory;
import org.cotato.csquiz.domain.auth.enums.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findAllByPolicyType(PolicyType policyType);

    List<Policy> findAllByIdIn(List<Long> ids);

    List<Policy> findAllByCategory(PolicyCategory category);
}
