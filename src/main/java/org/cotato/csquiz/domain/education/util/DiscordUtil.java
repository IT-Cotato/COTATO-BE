package org.cotato.csquiz.domain.education.util;

import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.cotato.csquiz.api.quiz.dto.ChoiceResponse;
import org.cotato.csquiz.domain.education.enums.QuizType;
import org.cotato.csquiz.domain.education.service.dto.RandomQuizResponse;

public class DiscordUtil {

    public static MessageEmbed getMultipleQuizEmbeds(RandomQuizResponse quiz) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("오늘의 퀴즈!");
        eb.setDescription("CS교육에서 풀었던 문제를 복습해봐요:)");
        eb.setColor(Color.cyan);
        eb.addField("타입", QuizType.MULTIPLE_QUIZ.getDescription(), false);
        eb.addField("문제: ", quiz.question(), false);

        eb.setImage(quiz.imageUrl());

        return eb.build();
    }

    public static List<Button> getButtons(List<ChoiceResponse> choices) {
        return choices.stream()
                .map(choice -> Button.primary(String.valueOf(choice.choiceId()), makeContent(choice.number(), choice.content())))
                .toList();
    }

    private static String makeContent(Integer choiceNumber, String content) {
        return choiceNumber + "번: " + content;
    }
}
