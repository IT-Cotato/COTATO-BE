package org.cotato.csquiz.domain.auth.repository;

import java.util.List;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
	List<Member> findAllWithFilters(Integer passedGenerationNumber, MemberPosition memberPosition, String name);

	Page<Member> findAllWithFiltersPageable(Integer passedGenerationNumber, MemberPosition memberPosition,
		MemberStatus memberStatus, String name, Pageable pageable);
}
