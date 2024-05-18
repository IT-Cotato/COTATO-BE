package cotato.csquiz.controller.dto.quiz;

import java.util.List;

public record QuizKingMembersResponse(
        List<KingMemberInfo> kingMemberInfos
) {
    public static QuizKingMembersResponse of(List<KingMemberInfo> infos) {
        return new QuizKingMembersResponse(infos);
    }
}
