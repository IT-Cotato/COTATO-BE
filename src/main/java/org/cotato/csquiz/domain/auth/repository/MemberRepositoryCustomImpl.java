package org.cotato.csquiz.domain.auth.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.AllArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.QMember;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MemberRepositoryCustomImpl  implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findAllWithFilters(Integer passedGenerationNumber, MemberPosition memberPosition, String name) {
        QMember qMember = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        if (passedGenerationNumber != null) {
            builder.and(qMember.passedGenerationNumber.eq(passedGenerationNumber));
        }

        if (memberPosition != null) {
            builder.and(qMember.position.eq(memberPosition));
        }

        if (name != null && !name.isEmpty()) {
            builder.and(qMember.name.containsIgnoreCase(name));
        }

        return queryFactory.selectFrom(qMember)
                .where(builder)
                .fetch();
    }
}
