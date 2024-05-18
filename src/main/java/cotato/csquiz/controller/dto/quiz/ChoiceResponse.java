package cotato.csquiz.controller.dto.quiz;

import cotato.csquiz.domain.entity.Choice;
import cotato.csquiz.domain.enums.ChoiceCorrect;

public record ChoiceResponse(
        Long choiceId,
        int number,
        String content,
        ChoiceCorrect isAnswer
) {
    public static ChoiceResponse forEducation(Choice choice) {
        return new ChoiceResponse(
                choice.getId(),
                choice.getChoiceNumber(),
                choice.getContent(),
                choice.getIsCorrect()
        );
    }

    public static ChoiceResponse forMember(Choice choice) {
        return new ChoiceResponse(
                choice.getId(),
                choice.getChoiceNumber(),
                choice.getContent(),
                ChoiceCorrect.SECRET
        );
    }
}
