package org.cotato.csquiz.domain.auth.service.component;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class MemberWriter {
    private final MemberRepository memberRepository;

    public void save(final Member member) {
        memberRepository.save(member);
    }
}
