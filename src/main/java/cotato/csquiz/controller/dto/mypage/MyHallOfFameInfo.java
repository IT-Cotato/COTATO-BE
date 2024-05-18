package cotato.csquiz.controller.dto.mypage;

import cotato.csquiz.domain.entity.Member;

public record MyHallOfFameInfo(
        Long memberId,
        long scorerCount,
        long answerCount
) {
    public static MyHallOfFameInfo of(Member member, long scorerCount, long answerCount) {
        return new MyHallOfFameInfo(
                member.getId(),
                scorerCount,
                answerCount
        );
    }
}
