package org.cotato.csquiz.domain.auth.service.component;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.entity.ProfileLink;
import org.cotato.csquiz.domain.auth.repository.ProfileLinkRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ProfileLinkWriter {
    private final ProfileLinkRepository profileLinkRepository;

    public void deleteAllByMember(final Member member) {
        profileLinkRepository.deleteAllByMember(member);
    }

    public void saveAllProfileLinks(List<ProfileLink> profileLinks) {
        profileLinkRepository.saveAll(profileLinks);
    }
}
