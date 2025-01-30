package org.cotato.csquiz.domain.auth.repository;

import org.cotato.csquiz.domain.auth.entity.MemberLeavingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLeavingRequestRepository extends JpaRepository<MemberLeavingRequest, Long> {
}
