package org.cotato.csquiz.api.quiz.dto;

import java.util.List;

public record QuizKingMembersResponse(
        List<KingMemberInfo> kingMemberInfos
) {
    public static QuizKingMembersResponse of(List<KingMemberInfo> infos) {
        return new QuizKingMembersResponse(infos);
    }
}
