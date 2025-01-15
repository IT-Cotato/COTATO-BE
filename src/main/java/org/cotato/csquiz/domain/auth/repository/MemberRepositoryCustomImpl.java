package org.cotato.csquiz.domain.auth.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.QMember;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryCustomImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(EntityManager em) {
        super(Member.class);
        this.queryFactory = new JPAQueryFactory(em);
    }

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
