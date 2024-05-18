package org.cotato.csquiz.api.mypage.dto;

import org.cotato.csquiz.domain.auth.entity.Member;

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

