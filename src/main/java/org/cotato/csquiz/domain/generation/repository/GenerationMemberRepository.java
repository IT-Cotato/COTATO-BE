package org.cotato.csquiz.domain.generation.repository;

import java.util.List;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface GenerationMemberRepository extends JpaRepository<GenerationMember, Long> {
    List<GenerationMember> findAllByGeneration(Generation generation);

    @Transactional
    @Modifying
    @Query("delete from GenerationMember g where g.id in :ids")
    void deleteAllByIdsInQuery(@Param("ids") List<Long> ids);

    boolean existsByGenerationAndMember(Generation generation, Member member);

    boolean existsByGenerationAndMemberIn(Generation generation, List<Member> member);
}
