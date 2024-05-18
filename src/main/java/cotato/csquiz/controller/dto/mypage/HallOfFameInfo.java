package cotato.csquiz.controller.dto.mypage;

import cotato.csquiz.domain.entity.Member;

public record HallOfFameInfo(
        Long memberId,
        String name,
        long count
) {
    public static HallOfFameInfo of(Member member, long count) {
        return new HallOfFameInfo(
                member.getId(),
                member.getName(),
                count
        );
    }
}

