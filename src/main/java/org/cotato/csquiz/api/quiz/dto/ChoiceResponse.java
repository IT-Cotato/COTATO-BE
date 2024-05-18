package org.cotato.csquiz.api.quiz.dto;

import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.enums.ChoiceCorrect;

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
