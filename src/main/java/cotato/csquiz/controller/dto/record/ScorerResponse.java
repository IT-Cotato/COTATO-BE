package cotato.csquiz.controller.dto.record;

import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Scorer;

public record ScorerResponse(
        Long scorerId,
        Long memberId,
        String memberName,
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
