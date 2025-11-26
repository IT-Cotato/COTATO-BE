package org.cotato.csquiz.domain.auth.repository;

import java.util.Optional;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.MemberLeavingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLeavingRequestRepository extends JpaRepository<MemberLeavingRequest, Long> {
	Optional<MemberLeavingRequest> findByMemberAndIsReactivatedFalse(Member member);
}
