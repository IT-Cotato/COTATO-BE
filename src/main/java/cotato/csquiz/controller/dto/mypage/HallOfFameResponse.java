package cotato.csquiz.controller.dto.mypage;

import java.util.List;

public record HallOfFameResponse(
        List<HallOfFameInfo> scorerInfo,
        List<HallOfFameInfo> answerInfo,
        MyHallOfFameInfo myInfo
) {
    public static HallOfFameResponse of(
            List<HallOfFameInfo> scorerInfo, List<HallOfFameInfo> answerInfo, MyHallOfFameInfo myInfo) {
        return new HallOfFameResponse(
                scorerInfo,
                answerInfo,
                myInfo
        );
    }
}
