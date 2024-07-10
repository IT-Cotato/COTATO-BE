package org.cotato.csquiz.domain.auth.repository;

import org.cotato.csquiz.domain.auth.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
