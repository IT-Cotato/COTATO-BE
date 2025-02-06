package org.cotato.csquiz.domain.auth.service.component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.MemberLeavingRequest;
import org.cotato.csquiz.domain.auth.repository.MemberLeavingRequestRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberLeavingRequestReader {

    private final MemberLeavingRequestRepository memberLeavingRequestRepository;

    public MemberLeavingRequest getLeavingRequestByMember(final Member member) {
        return memberLeavingRequestRepository.findByMemberAndIsReactivatedFalse(member)
                .orElseThrow(() -> new EntityNotFoundException("해당 부원의 요청이 존재하지 않습니다."));
    }
}
