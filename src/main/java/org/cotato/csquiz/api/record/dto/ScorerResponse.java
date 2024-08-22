package org.cotato.csquiz.api.record.dto;

import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.auth.entity.Member;

public record ScorerResponse(
        Long scorerId,
        Long memberId,
        String name,
        String backFourNumber
) {
    public static ScorerResponse of(Scorer scorer, Member member, String backFourNumber) {
        return new ScorerResponse(
                scorer.getId(),
                member.getId(),
                member.getName(),
                backFourNumber
        );
    }
}
