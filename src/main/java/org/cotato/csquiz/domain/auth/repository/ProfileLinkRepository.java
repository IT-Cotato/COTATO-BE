package org.cotato.csquiz.domain.auth.repository;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.ProfileLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLinkRepository extends JpaRepository<ProfileLink, Long> {
    void deleteAllByMember(Member member);
}
