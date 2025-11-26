package org.cotato.csquiz.domain.education.repository;

import java.util.List;

import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.KingMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KingMemberRepository extends JpaRepository<KingMember, Long> {
	List<KingMember> findAllByEducation(Education education);

	boolean existsByEducation(Education education);
}
