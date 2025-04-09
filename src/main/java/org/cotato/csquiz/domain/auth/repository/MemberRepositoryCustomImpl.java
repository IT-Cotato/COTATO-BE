package org.cotato.csquiz.domain.auth.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.QMember;
import org.cotato.csquiz.domain.auth.enums.MemberPosition;
import org.cotato.csquiz.domain.auth.enums.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

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

    @Override
    public Page<Member> findAllWithFiltersPageable(Integer passedGenerationNumber, MemberPosition memberPosition,
                                                   MemberStatus memberStatus, String name,
                                                   Pageable pageable) {
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
        if (memberStatus != null) {
            builder.and(qMember.status.eq(memberStatus));
        }

        List<Member> results = queryFactory.selectFrom(qMember)
                .where(builder)
                .orderBy(qMember.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(qMember.count())
                .from(qMember)
                .where(builder);

        return PageableExecutionUtils.getPage(results, pageable, () -> {
            Long count = countQuery.fetchOne();
            return count != null ? count : 0;
        });
    }
}
