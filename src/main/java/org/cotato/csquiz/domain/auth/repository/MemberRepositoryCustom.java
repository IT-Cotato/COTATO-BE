package org.cotato.csquiz.domain.auth.repository;

import java.util.List;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;

public interface MemberRepositoryCustom {
    List<Member> findAllWithFilters(Integer passedGenerationNumber, MemberPosition memberPosition, String name);
}
